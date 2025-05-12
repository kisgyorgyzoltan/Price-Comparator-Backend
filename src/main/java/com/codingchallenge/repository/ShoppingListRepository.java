package com.codingchallenge.repository;

import com.codingchallenge.model.ShoppingList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShoppingListRepository extends MongoRepository<ShoppingList, String> {
}
