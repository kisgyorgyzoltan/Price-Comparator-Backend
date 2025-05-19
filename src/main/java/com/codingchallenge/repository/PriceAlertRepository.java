package com.codingchallenge.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.codingchallenge.model.PriceAlert;

import java.util.List;

public interface PriceAlertRepository extends MongoRepository<PriceAlert, String> {
    List<PriceAlert> findByUserId(String userId);
}
