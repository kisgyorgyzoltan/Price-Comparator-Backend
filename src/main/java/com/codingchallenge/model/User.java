package com.codingchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    private String name;
    private String password;
    private List<CartItem> shoppingCart = List.of();
    private LocalDateTime  lastCartUpdate;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItem {
        private String productId;
        private int quantity;
    }
}
