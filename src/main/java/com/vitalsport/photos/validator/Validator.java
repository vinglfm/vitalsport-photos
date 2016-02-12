package com.vitalsport.photos.validator;

import java.util.function.Predicate;

public interface Validator {
    <T> void validate(Predicate<T> predicate, T validateObject, String message);
}
