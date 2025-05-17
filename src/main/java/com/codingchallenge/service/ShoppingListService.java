package com.codingchallenge.service;

import com.codingchallenge.dto.internal.BestPriceResult;
import com.codingchallenge.model.Product;
import com.codingchallenge.model.ShoppingList;
import com.codingchallenge.model.User;
import com.codingchallenge.model.User.CartItem;
import com.codingchallenge.repository.PriceEntryRepository;
import com.codingchallenge.repository.ShoppingListRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class ShoppingListService {
    private final ShoppingListRepository shoppingListRepository;

    private final PriceEntryRepository priceEntryRepository;


    public ShoppingListService(ShoppingListRepository shoppingListRepository, PriceEntryRepository priceEntryRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.priceEntryRepository = priceEntryRepository;
    }


    public List<ShoppingList> getAllShoppingLists() {
        return shoppingListRepository.findAll();
    }

    public ShoppingList getShoppingListById(String id) {
        return shoppingListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shopping list not found with id: " + id));
    }

    public void deleteShoppingList(String id) {
        Optional<ShoppingList> shoppingList = shoppingListRepository.findById(id);
        shoppingList.ifPresent(shoppingListRepository::delete);
    }

    public ShoppingList getBestPrices(String userId, List<CartItem> cartItems) {
        List<String> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .toList();
        LocalDate today = LocalDate.now();
        List<BestPriceResult> bestPriceResults = priceEntryRepository.getBestPrices(today, productIds);

        // set the quantity for each product in the cart
        for (CartItem cartItem : cartItems) {
            String productId = cartItem.getProductId();
            int quantity = cartItem.getQuantity();
            for (BestPriceResult result : bestPriceResults) {
                if (result.getProductId().equals(productId)) {
                    result.setQuantity(quantity);
                    break;
                }
            }
        }

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

    public List<ShoppingList> getShoppingListsByUserId(User user) {
        String userId = user.getId();
        return shoppingListRepository.findByUserId(userId);
    }
}