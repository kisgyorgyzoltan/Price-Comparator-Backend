package com.codingchallenge.repository;

import com.codingchallenge.model.PriceEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceEntryRepository extends MongoRepository<PriceEntry, String> {
    List<PriceEntry> findByProductId(String productId);
}
