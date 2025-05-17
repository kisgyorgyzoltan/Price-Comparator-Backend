package com.codingchallenge.dto.incoming;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddProductToCartDto {
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
    @NotNull
    private String productId;

    @Positive
    @NotNull
    private Integer quantity;
}
