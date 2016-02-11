package com.vitalsport.photos.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultipartFileUploadValidator implements UploadValidator<MultipartFile>{

    @Override
    public void validate(MultipartFile multipartFile) {
        validateEmptyFile(multipartFile);

        validateContentType(multipartFile);
    }

    private void validateContentType(MultipartFile multipartFile) {
        //TODO: possibly use other way to find out a content type
        String contentType = multipartFile.getContentType();
        if(!contentType.startsWith("image")) {
            throw new IllegalArgumentException(String.format("ContentType: %s is not supported.", contentType));
        }
    }

    private void validateEmptyFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("Uploading an empty file.");
        }
    }
}
