package com.codingchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "price_entries")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceEntry {
    @Id
    private String id;
    private String productId;
    private double price;
    private String currency;

    private String storeName;
    private LocalDate date;
}
