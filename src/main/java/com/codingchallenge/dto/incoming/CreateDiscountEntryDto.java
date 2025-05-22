package com.codingchallenge.dto.incoming;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDiscountEntryDto {
    @NotNull(message = "Product ID cannot be null")
    @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
    private String productId;

    @NotNull(message = "From date cannot be null")
    private LocalDate fromDate;

    @NotNull(message = "To date cannot be null")
    private LocalDate toDate;

    @NotNull(message = "Percentage of discount cannot be null")
    @PositiveOrZero(message = "Percentage of discount must be a positive number or zero.")
    private int percentageOfDiscount;

    @NotNull(message = "Store name cannot be null")
    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Store name must be alphanumeric and between 1 and 100 characters.")
    private String storeName;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;
}
