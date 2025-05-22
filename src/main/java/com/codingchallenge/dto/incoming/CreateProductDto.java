package com.codingchallenge.dto.incoming;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateProductDto {
    @NotNull(message = "Product ID cannot be null")
    @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
    private String productId;

    @NotNull(message = "Product name cannot be null")
    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Product name must be alphanumeric and between 1 and 100 characters.")
    private String productName;

    @NotNull(message = "Product description cannot be null")
    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Product description must be alphanumeric and between 1 and 100 characters.")
    private String productCategory;

    @NotNull(message = "Brand cannot be null")
    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Brand name must be alphanumeric and between 1 and 100 characters.")
    private String brand;

    @NotNull(message = "Product type cannot be null")
    @Positive(message = "Product type must be a positive number.")
    private double packageQuantity;

    @NotNull(message = "Package unit cannot be null")
    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Package unit must be alphanumeric and between 1 and 100 characters.")
    private String packageUnit;
}
