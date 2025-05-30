package com.codingchallenge.dto.incoming;

import com.codingchallenge.validation.ValidValuePerUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
@ValidValuePerUnit
public class CreatePriceEntryDto {
    @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Product ID must be alphanumeric and between 1 and 20 characters.")
    @NotNull(message = "Product ID cannot be null")
    private String productId;

    @Positive
    @NotNull(message = "Price cannot be null")
    private double price;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO currency code.")
    @NotNull(message = "Currency cannot be null")
    private String currency;

    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Store name must be alphanumeric and between 1 and 100 characters.")
    @NotNull(message = "Store name cannot be null")
    private String storeName;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotNull(message = "Package unit cannot be null")
    @Pattern(regexp = "^[A-Za-z0-9\\s]{1,100}$", message = "Package unit must be alphanumeric and between 1 and 100 characters.")
    private String packageUnit;

    @NotNull(message = "Package quantity cannot be null")
    @Positive(message = "Product type must be a positive number.")
    private double packageQuantity;

    @NotNull(message = "Value per unit display cannot be null")
    private String valuePerUnitDisplay;

    @NotNull(message = "Value per unit cannot be null")
    @Positive(message = "Value per unit must be a positive number.")
    private Double valuePerUnit;
}
