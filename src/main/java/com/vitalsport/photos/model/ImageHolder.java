package com.vitalsport.photos.model;

import lombok.Data;
import org.springframework.http.MediaType;

@Data
public class ImageHolder {
    private final MediaType mediaType;
    private final byte[] data;
}
