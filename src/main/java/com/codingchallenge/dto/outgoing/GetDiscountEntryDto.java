package com.codingchallenge.dto.outgoing;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetDiscountEntryDto {
    private String id;
    private String productId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int percentageOfDiscount;
    private String storeName;
    private LocalDate date;
}
