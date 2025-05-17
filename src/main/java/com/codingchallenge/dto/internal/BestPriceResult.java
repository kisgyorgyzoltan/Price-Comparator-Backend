package com.codingchallenge.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BestPriceResult {
    private String productId;
    private String storeName;
    private Double originalPrice;
    private Double effectivePrice;
    private String currency;
    private Integer discountApplied;
    private Date priceDate;
    private Integer quantity;
}

