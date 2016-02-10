package com.vitalsport.photos.validator;

public interface UploadValidator<T> {
    void validate(T validateObject);
}
