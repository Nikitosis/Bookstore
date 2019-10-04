package com.utils;

import org.springframework.stereotype.Component;

import javax.validation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectValidator {
    private static ValidatorFactory validatorFactory= Validation.buildDefaultValidatorFactory();

    public static <T> void validateFields(T object) throws ValidationException {
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> failedValidations = validator.validate(object);

        if (!failedValidations.isEmpty()) {
            List<String> allErrors = failedValidations.stream().map(failure -> failure.getMessage())
                    .collect(Collectors.toList());
            throw new ValidationException("Validation failure; Invalid request.");
        }
    }
}
