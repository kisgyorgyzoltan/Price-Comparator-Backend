package com.codingchallenge.service;

import com.codingchallenge.model.PriceEntry;
import com.codingchallenge.model.Product;
import com.codingchallenge.repository.PriceEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceEntryService {

    private final PriceEntryRepository priceEntryRepository;

    public PriceEntryService(PriceEntryRepository priceEntryRepository) {
        this.priceEntryRepository = priceEntryRepository;
    }

    List<PriceEntry> getPriceEntriesForProduct(Product product) {
        return priceEntryRepository.findByProductId(product.getProductId());
    }
}
