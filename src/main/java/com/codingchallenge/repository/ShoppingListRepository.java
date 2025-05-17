package com.codingchallenge.repository;

import com.codingchallenge.model.ShoppingList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShoppingListRepository extends MongoRepository<ShoppingList, String> {
    List<ShoppingList> findByUserId(String userId);
}
