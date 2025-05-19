package com.codingchallenge.dto.incoming;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreatePriceAlertDto {
    @NotNull(message = "Target price cannot be null")
    @Positive(message = "Target price must be a positive number")
    private Double targetPrice;

    @NotNull(message = "Product ID cannot be null")
    @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
    private String productId;

    @NotNull(message = "User ID cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,20}$", message = "Name must be alphanumeric and between 3 and 20 characters long")
    private String userId;

    @NotNull(message = "Message cannot be null")
    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Message must be alphanumeric and between 1 and 100 characters.")
    private String message;
}
