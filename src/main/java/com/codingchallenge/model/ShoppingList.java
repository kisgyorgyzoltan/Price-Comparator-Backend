package com.codingchallenge.model;

import com.codingchallenge.dto.internal.BestProductPriceResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Document(collection = "shopping_lists")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingList {
    @Id
    private String id;
    private String userId;
    private String name;
    private LocalDate createdDate;
    // storeName -> list of products from that store
    private HashMap<String, List<BestProductPriceResult>> products;
}



