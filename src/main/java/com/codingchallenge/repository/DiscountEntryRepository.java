package com.codingchallenge.repository;

import com.codingchallenge.model.DiscountEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscountEntryRepository extends MongoRepository<DiscountEntry, String> {
    List<DiscountEntry> findByProductId(String productId);

    List<DiscountEntry> findByDateAfter(LocalDate localDate);
}
