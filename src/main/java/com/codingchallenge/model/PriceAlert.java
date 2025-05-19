package com.codingchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "price_alerts")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PriceAlert {
    @Id
    private String id;
    private Double targetPrice;
    private String productId;
    private String userId;
    private String message;
}
