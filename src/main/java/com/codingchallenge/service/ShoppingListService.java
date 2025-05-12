package com.codingchallenge.service;

import com.codingchallenge.dto.internal.BestPriceResult;
import com.codingchallenge.model.Product;
import com.codingchallenge.model.ShoppingList;
import com.codingchallenge.repository.ShoppingListRepository;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShoppingListService {
    private final MongoTemplate mongoTemplate;

    private final MongoConverter mongoConverter;

    private final ShoppingListRepository shoppingListRepository;


    public ShoppingListService(MongoTemplate mongoTemplate, MongoConverter mongoConverter, ShoppingListRepository shoppingListRepository) {
        this.mongoTemplate = mongoTemplate;
        this.mongoConverter = mongoConverter;
        this.shoppingListRepository = shoppingListRepository;
    }

    public ShoppingList getBestPrices(String userId, List<Product> products) {
        List<Document> pipeline = buildPipeline(products);

        // Execute the aggregation pipeline
        List<Document> rawResults = mongoTemplate.getDb()
                .getCollection("price_entries")
                .aggregate(pipeline)
                .into(new ArrayList<>());

        // Convert the raw results to BestPriceResult objects
        List<BestPriceResult> bestPriceResults = rawResults.stream()
                .map(doc -> mongoConverter.read(BestPriceResult.class, doc))
                .toList();

        // Create a ShoppingList with the best prices
        return createShoppingList(bestPriceResults, userId);
    }

    private ShoppingList createShoppingList(List<BestPriceResult> bestPriceResults, String userId) {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setProducts(new HashMap<>());
        shoppingList.setCreatedDate(LocalDate.now());
        shoppingList.setUserId(userId);
        shoppingList.setName("Best_Prices_" + LocalDate.now() + "_" + UUID.randomUUID());
        for (BestPriceResult result : bestPriceResults) {
            String storeName = result.getStoreName();
            if (!shoppingList.getProducts().containsKey(storeName)) {
                shoppingList.getProducts().put(storeName, new ArrayList<>());
            }
            shoppingList.getProducts().get(storeName).add(result);
        }
        return shoppingListRepository.save(shoppingList);
    }

    private static List<Document> buildPipeline(List<Product> products) {
        // The alternative to using Document class is to use Aggregation class
        // but the documentation is not clear on how to use it (not enough examples)
        Document matchStage;
        if (products == null || products.isEmpty()) {
            matchStage = new Document("$match", new Document("date", new Document("$lte", new Date())));
        } else {
            List<String> productIds = products.stream()
                    .map(Product::getProductId)
                    .collect(Collectors.toList());
            matchStage = new Document("$match", new Document("date", new Document("$lte", new Date()))
                    .append("productId", new Document("$in", productIds)));
        }

        Document sortByStoreNameAndDate = new Document("$sort", new Document("storeName", 1).append("date", -1));

        Document groupByProductIdAndStoreName = new Document("$group", new Document("_id", new Document("productId", "$productId")
                .append("storeName", "$storeName"))
                .append("latestPriceEntry", new Document("$first", "$$ROOT")));

        Document lookupWithDiscounts = new Document("$lookup", new Document("from", "discount_entries")
                .append("let", new Document("productId", "$_id.productId")
                        .append("storeName", "$_id.storeName")
                        .append("priceDate", "$latestPriceEntry.date"))
                .append("pipeline", Arrays.asList(
                        new Document("$match", new Document("$expr", new Document("$and", Arrays.asList(
                                new Document("$eq", Arrays.asList("$productId", "$$productId")),
                                new Document("$eq", Arrays.asList("$storeName", "$$storeName")),
                                new Document("$lte", Arrays.asList("$fromDate", "$$priceDate")),
                                new Document("$gte", Arrays.asList("$toDate", "$$priceDate"))
                        )))),
                        new Document("$sort", new Document("date", -1)),
                        new Document("$limit", 1)
                ))
                .append("as", "applicableDiscount"));

        Document addDiscountField = new Document("$addFields", new Document("discount", new Document("$arrayElemAt", Arrays.asList("$applicableDiscount", 0))));
        Document addEffectivePrice = new Document("$addFields", new Document("effectivePrice", new Document("$cond", new Document("if", new Document("$and", Arrays.asList(
                new Document("$gt", Arrays.asList(new Document("$size", "$applicableDiscount"), 0)),
                new Document("$ne", Arrays.asList("$discount.percentageOfDiscount", null))
        )))
                .append("then", new Document("$multiply", Arrays.asList(
                        "$latestPriceEntry.price",
                        new Document("$subtract", Arrays.asList(1, new Document("$divide", Arrays.asList("$discount.percentageOfDiscount", 100))))
                )))
                .append("else", "$latestPriceEntry.price")
        )));

        Document sortByEffectivePrice = new Document("$sort", new Document("effectivePrice", 1));

        Document groupByProductId = new Document("$group", new Document("_id", "$_id.productId")
                .append("bestPriceEntry", new Document("$first", "$$ROOT")));

        Document projectStage = new Document("$project", new Document("_id", 0)
                .append("productId", "$_id")
                .append("storeName", "$bestPriceEntry._id.storeName")
                .append("effectivePrice", "$bestPriceEntry.effectivePrice")
                .append("originalPrice", "$bestPriceEntry.latestPriceEntry.price")
                .append("currency", "$bestPriceEntry.latestPriceEntry.currency")
                .append("discountApplied", new Document("$cond", new Document("if", new Document("$gt", Arrays.asList(new Document("$size", "$bestPriceEntry.applicableDiscount"), 0))).append("then", "$bestPriceEntry.discount.percentageOfDiscount")
                        .append("else", 0)))
                .append("priceDate", "$bestPriceEntry.latestPriceEntry.date"));


        return Arrays.asList(
            matchStage,
            sortByStoreNameAndDate,
            groupByProductIdAndStoreName,
            lookupWithDiscounts,
            addDiscountField,
            addEffectivePrice,
            sortByEffectivePrice,
            groupByProductId,
            projectStage
        );
    }
}