package com.codingchallenge.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValuePerUnitValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidValuePerUnit {
    String message() default "Invalid value per unit or display string";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

