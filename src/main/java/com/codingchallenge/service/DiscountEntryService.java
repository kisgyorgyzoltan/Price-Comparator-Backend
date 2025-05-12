package com.codingchallenge.service;

import com.codingchallenge.model.DiscountEntry;
import com.codingchallenge.model.Product;
import com.codingchallenge.repository.DiscountEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountEntryService {
    private final DiscountEntryRepository discountEntryRepository;

    public DiscountEntryService(DiscountEntryRepository discountEntryRepository) {
        this.discountEntryRepository = discountEntryRepository;
    }

    List<DiscountEntry> getDiscountEntriesForProduct(Product product) {
        return discountEntryRepository.findByProductId(product.getProductId());
    }
}
