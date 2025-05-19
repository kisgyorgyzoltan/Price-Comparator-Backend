package com.codingchallenge.dto.outgoing;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetPriceEntryDto {
    private String productId;
    private double price;
    private String currency;
    private String storeName;
    private LocalDate date;
    private Double packageQuantity;
    private String packageUnit;
    private String valuePerUnitDisplay;
    private Double valuePerUnit;
}
