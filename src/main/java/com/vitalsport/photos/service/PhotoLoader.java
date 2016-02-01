package com.vitalsport.photos.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface PhotoLoader {
    boolean loadPhoto(String userId, String album, String fileName, MultipartFile file);

    Collection<String> getUserAlbums(String userId);

    Collection<String> getUserPhotos(String userId, String album);
}
