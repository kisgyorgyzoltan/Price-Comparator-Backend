package com.codingchallenge.dto.internal;

import com.codingchallenge.model.PriceEntry;
import com.codingchallenge.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Data
public class PriceParseResult {
    private List<PriceEntry> priceEntries;
    private Set<Product> products;
}
