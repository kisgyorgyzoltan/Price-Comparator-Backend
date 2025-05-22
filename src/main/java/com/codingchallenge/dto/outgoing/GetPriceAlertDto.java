package com.codingchallenge.dto.outgoing;

import lombok.Data;

@Data
public class GetPriceAlertDto {
    private String id;
    private Double targetPrice;
    private String productId;
    private String userId;
    private String message;
}
