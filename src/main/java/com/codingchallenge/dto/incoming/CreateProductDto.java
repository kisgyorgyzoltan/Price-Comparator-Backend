package com.codingchallenge.dto.incoming;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateProductDto {
    @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")

    private String productId;

    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Product name must be alphanumeric and between 1 and 100 characters.")
    private String productName;

    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Product description must be alphanumeric and between 1 and 100 characters.")
    private String productCategory;

    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Brand name must be alphanumeric and between 1 and 100 characters.")
    private String brand;

    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Product type must be alphanumeric and between 1 and 100 characters.")
    @Positive(message = "Product type must be a positive number.")
    private double packageQuantity;

    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Package unit must be alphanumeric and between 1 and 100 characters.")
    private String packageUnit;
}
