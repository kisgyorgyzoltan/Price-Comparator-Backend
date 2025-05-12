package com.codingchallenge.dto.internal;

import com.codingchallenge.model.DiscountEntry;
import com.codingchallenge.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Data
public class DiscountParseResult {
    private List<DiscountEntry> discountEntries;
    private Set<Product> products;
}
