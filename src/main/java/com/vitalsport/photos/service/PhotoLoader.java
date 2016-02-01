package com.vitalsport.photos.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Collection;

public interface PhotoLoader {
    boolean loadPhoto(String userId, String album, String fileName, MultipartFile file);

    Collection<String> getUserAlbums(String userId);
}
