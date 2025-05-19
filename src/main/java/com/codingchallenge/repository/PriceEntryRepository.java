package com.codingchallenge.repository;

import com.codingchallenge.dto.internal.BestProductPriceResult;
import com.codingchallenge.dto.outgoing.GetPriceHistoryDto;
import com.codingchallenge.model.PriceEntry;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PriceEntryRepository extends MongoRepository<PriceEntry, String> {
    List<PriceEntry> findByProductId(String productId, Sort sort);

    @Aggregation(pipeline = {
            """
            {
                $match: {
                  date: { $lte: ?0 },
                  "$expr": {
                    "$and": [
                      {
                        "$or": [
                          { "$eq": [?1, null] },
                          { $in: ["$productId", ?1 ] }
                        ]
                      }
                    ]
                  }
                }
            }
            """,
            """
            {
                $sort: {
                  storeName: 1,
                  date: -1
                }
            }
            """,
            """
            {
               $group: {
                 _id: { productId: "$productId", storeName: "$storeName" },
                 latestPriceEntry: { $first: "$$ROOT" }
               }
            }
            """,
            """
            {
                $lookup: {
                  from: "discount_entries",
                  let: {
                    productId: "$_id.productId",
                    storeName: "$_id.storeName",
                    priceDate: "$latestPriceEntry.date"
                  },
                  pipeline: [
                    {
                      $match: {
                        $expr: {
                          $and: [
                            { $eq: ["$productId", "$$productId"] },
                            { $eq: ["$storeName", "$$storeName"] },
                            { $lte: ["$fromDate", ?0]},
                            { $gte: ["$toDate", ?0]}
                          ]
                        }
                      }
                    },
                    { $sort: { date: -1 } },
                    { $limit: 1 }
                  ],
                  as: "applicableDiscount"
                }
            }
            """,
            """
            {
                $addFields: {
                  discount: { $arrayElemAt: ["$applicableDiscount", 0] }
                }
            }
            """,
            """
            {
                $addFields: {
                  effectivePrice: {
                    $cond: {
                      if: {
                        $and: [
                          { $gt: [{ $size: "$applicableDiscount" }, 0] },
                          { $ne: ["$discount.percentageOfDiscount", null] }
                        ]
                      },
                      then: {
                        $multiply: [
                          "$latestPriceEntry.price",
                          {
                            $subtract: [
                              1,
                              { $divide: ["$discount.percentageOfDiscount", 100] }
                            ]
                          }
                        ]
                      },
                      else: "$latestPriceEntry.price"
                    }
                  }
                }
            }
            """,
            """
            {
                $sort: { effectivePrice: 1 }
            }
            """,
            """
            {
                $group: {
                  _id: "$_id.productId",
                  bestPriceEntry: { $first: "$$ROOT" }
                }
            }
            """,
            """
            {
                $project: {
                  _id: 0,
                  productId: "$_id",
                  storeName: "$bestPriceEntry._id.storeName",
                  effectivePrice: "$bestPriceEntry.effectivePrice",
                  originalPrice: "$bestPriceEntry.latestPriceEntry.price",
                  currency: "$bestPriceEntry.latestPriceEntry.currency",
                  discountApplied: {
                    $cond: {
                      if: { $gt: [{ $size: "$bestPriceEntry.applicableDiscount" }, 0] },
                      then: "$bestPriceEntry.discount.percentageOfDiscount",
                      else: 0
                    }
                  },
                  priceDate: "$bestPriceEntry.latestPriceEntry.date"
                }
            }
            """

    })
    List<BestProductPriceResult> generateBestPrices(LocalDate date, List<String> productIds);

    @Aggregation(pipeline = {
            """
            {
                "$lookup": {
                  "from": "products",
                  "localField": "productId",
                  "foreignField": "_id",
                  "as": "product"
                }
            }
            """,
            """
            {
                "$unwind": "$product"
            }
            """,
            """
            {
                "$match": {
                  "$expr": {
                    "$and": [
                      {
                        "$or": [
                          { "$eq": [?0, null] },
                          { "$eq": ["$productId", ?0] }
                        ]
                      },
                      {
                        "$or": [
                          { "$eq": [?1, null] },
                          { "$eq": ["$storeName", ?1] }
                        ]
                      },
                      {
                        "$or": [
                          { "$eq": [?2, null] },
                          { "$eq": ["$product.productCategory", ?2] }
                        ]
                      },
                      {
                        "$or": [
                          { "$eq": [?3, null] },
                          { "$eq": ["$product.brand", ?3] }
                        ]
                      }
                    ]
                  }
                }
            }
            """,
            """
            {
                "$group": {
                  "_id": {
                    "productId": "$productId",
                    "productName": "$product.productName"
                  },
                  "priceHistory": {
                    "$push": {
                      "storeName": "$storeName",
                      "date": "$date",
                      "price": "$price",
                      "currency": "$currency",
                    }
                  }
                }
            }
            """,
            """
            {
                "$project": {
                  "_id": 0,
                  "productId": "$_id.productId",
                  "productName": "$_id.productName",
                  "productCategory": "$product.productCategory",
                  "brand": "$product.brand",
                  "priceHistory": 1
                }
             }
            """
    })
    List<GetPriceHistoryDto> getPriceHistory(String productId, String storeName, String productCategory, String brand);
}
