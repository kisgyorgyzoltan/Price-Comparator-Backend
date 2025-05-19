package com.codingchallenge.validation;

import com.codingchallenge.dto.incoming.CreatePriceEntryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValuePerUnitValidator implements ConstraintValidator<ValidValuePerUnit, CreatePriceEntryDto> {

    @Override
    public boolean isValid(CreatePriceEntryDto dto, ConstraintValidatorContext context) {
        if (dto == null) return true;

        double price = dto.getPrice();
        double quantity = dto.getPackageQuantity();
        String currency = dto.getCurrency();
        String packageUnit = dto.getPackageUnit();

        Double expectedValuePerUnit = price / quantity;
        String expectedDisplay = String.format("%.2f %s/%s", expectedValuePerUnit, currency, packageUnit);

        boolean valueMatch = dto.getValuePerUnit() == null || Math.abs(dto.getValuePerUnit() - expectedValuePerUnit) < 0.0001;
        boolean displayMatch = dto.getValuePerUnitDisplay() == null || dto.getValuePerUnitDisplay().equals(expectedDisplay);

        if (!valueMatch || !displayMatch) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Invalid valuePerUnit or valuePerUnitDisplay. Expected format: \"" + expectedDisplay + "\""
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}

