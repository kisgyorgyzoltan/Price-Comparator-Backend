package com.codingchallenge.dto.outgoing;

import lombok.Data;

import java.util.List;

@Data
public class GetPriceHistoryDto {
    private String productId;
    private String productName;
    private String productCategory;
    private String brand;
    private List<PriceHistory> priceHistory;

    @Data
    private static class PriceHistory {
        private String storeName;
        private String date;
        private double price;
        private String currency;
    }
}
