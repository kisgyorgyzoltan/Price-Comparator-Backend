package com.codingchallenge.validation;

import com.codingchallenge.dto.incoming.CreatePriceEntryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValuePerUnitValidator implements ConstraintValidator<ValidValuePerUnit, CreatePriceEntryDto> {

    @Override
    public boolean isValid(CreatePriceEntryDto dto, ConstraintValidatorContext context) {
        if (dto == null) return false;

        double price = dto.getPrice();
        double quantity = dto.getPackageQuantity();
        String currency = dto.getCurrency();
        String packageUnit = dto.getPackageUnit();

        Double expectedValuePerUnit = price / quantity;
        String expectedDisplay = String.format("%.2f %s/%s", expectedValuePerUnit, currency, packageUnit);

        boolean valueMatch = Math.abs(dto.getValuePerUnit() - expectedValuePerUnit) < 0.0001;
        boolean displayMatch = dto.getValuePerUnitDisplay().equals(expectedDisplay);

        return valueMatch && displayMatch;
    }
}

