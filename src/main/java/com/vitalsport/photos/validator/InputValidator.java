package com.vitalsport.photos.validator;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class InputValidator implements Validator {

    public <T> void validate(Predicate<T> predicate, T validateObject, String message) {
        if (predicate.test(validateObject)) {
            throw new IllegalArgumentException(message);
        }
    }
}
