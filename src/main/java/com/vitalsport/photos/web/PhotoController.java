package com.vitalsport.photos.web;

import com.vitalsport.photos.model.ImageHolder;
import com.vitalsport.photos.service.PhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.ResponseEntity.*;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@Controller
@CrossOrigin(origins = "*")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @RequestMapping(value = "/{userId}/upload", method = POST)
    public ResponseEntity<?> uploadImage(@PathVariable String userId,
                                              @RequestParam String album,
                                              @RequestParam(value = "file") MultipartFile file) {
        log.debug("User: {} uploading photo: {}", userId, file.getOriginalFilename());

        photoService.uploadImage(userId, album, file.getOriginalFilename(), file);
        return noContent().build();
    }

    @RequestMapping(value = "/{userId}/album", method = POST)
    public ResponseEntity<?> createAlbum(@PathVariable String userId,
                                              @RequestParam String album) {
        log.debug("User: {} creating an album: {}", userId, album);

        photoService.createAlbum(userId, album);

        return noContent().build();
    }

    @RequestMapping(value = "/{userId}/image", method = DELETE)
    public ResponseEntity<?> deleteImage(@PathVariable String userId,
                                              @RequestParam String album,
                                              @RequestParam String image) {
        log.debug("User: {} deleting an image: {} from album: {}", userId, image, album);

        photoService.deleteImage(userId, album, image);

        return noContent().build();
    }

    @RequestMapping(value = "/{userId}/album", method = DELETE)
    public ResponseEntity<?> deleteAlbum(@PathVariable String userId,
                                              @RequestParam String album) {
        log.debug("User: {} deleting an album: {}", userId, album);

        //TODO: provide a possibility to keep images
        photoService.deleteAlbum(userId, album);

        return noContent().build();
    }

    //TODO: create meta-data server for clustering
    @RequestMapping(value = "/{userId}/albums", method = GET)
    public ResponseEntity<?> getUserAlbums(@PathVariable String userId) {
        log.debug("Retrieving info about albums for user: {}", userId);

        return ok(photoService.getUserAlbums(userId));
    }

    @RequestMapping(value = "/{userId}/{album}", method = GET)
    public ResponseEntity<?> getAlbumsImageList(@PathVariable String userId, @PathVariable String album) {
        log.debug("Retrieving info about photo for user: {}, album: {}", userId, album);

        return ok(photoService.getUserPhotos(userId, album));
    }

    @RequestMapping(value = "/{userId}/image", method = GET)
    public ResponseEntity<?> getImage(@PathVariable String userId,
                                      @RequestParam String album, @RequestParam String image) throws IOException {
        log.debug("Retrieving image: {} for user: {}, album: {}", image, userId, album);

        ImageHolder imageHolder = photoService.downloadImage(userId, album, image);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(imageHolder.getMediaType());

        return new ResponseEntity<>(imageHolder.getData(), httpHeaders, HttpStatus.OK);
    }
}
