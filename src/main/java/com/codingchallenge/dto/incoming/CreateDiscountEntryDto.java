package com.codingchallenge.dto.incoming;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDiscountEntryDto {
    @NotNull(message = "Product ID cannot be null")
    @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "Product ID must be a valid MongoDB ObjectId.")
    private String productId;

    @NotNull(message = "From date cannot be null")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "From date must be in the format YYYY-MM-DD.")
    private LocalDate fromDate;

    @NotNull(message = "To date cannot be null")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "To date must be in the format YYYY-MM-DD.")
    private LocalDate toDate;

    @NotNull(message = "Percentage of discount cannot be null")
    @Pattern(regexp = "^(100|[1-9][0-9]?)$", message = "Percentage of discount must be between 0 and 100.")
    private int percentageOfDiscount;

    @NotNull(message = "Store name cannot be null")
    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Store name must be alphanumeric and between 1 and 100 characters.")
    private String storeName;

    @NotNull(message = "Date cannot be null")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in the format YYYY-MM-DD.")
    private LocalDate date;
}
