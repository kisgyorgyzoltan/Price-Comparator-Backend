package com.codingchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "discount_entries")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountEntry {
    @Id
    private String id;
    private String productId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int percentageOfDiscount;

    private String storeName;
    private LocalDate date;

}

