package com.vitalsport.photos.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileLoader {
    boolean loadFile(String userId, String fileName, MultipartFile file);
}
