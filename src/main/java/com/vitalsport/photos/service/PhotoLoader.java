package com.vitalsport.photos.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

public interface PhotoLoader {
    boolean uploadPhoto(String userId, String album, String fileName, MultipartFile file);

    byte[] downloadPhoto(String userId, String album, String fileName) throws IOException;

    Collection<String> getUserAlbums(String userId);

    Collection<String> getUserPhotos(String userId, String album);
}
