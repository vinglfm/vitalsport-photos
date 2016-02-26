package com.vitalsport.photos.service;

import com.vitalsport.photos.model.ImageHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

public interface PhotoLoader {
    void uploadImage(String userId, String album, String fileName, MultipartFile file);

    ImageHolder downloadImage(String userId, String album, String fileName);

    Collection<String> getUserAlbums(String userId);

    Collection<String> getUserPhotos(String userId, String album);

    void createAlbum(String userId, String album);

    void deleteImage(String userId, String album, String image);

    void deleteAlbum(String userId, String album);
}
