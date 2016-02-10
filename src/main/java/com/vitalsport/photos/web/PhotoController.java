package com.vitalsport.photos.web;

import com.vitalsport.photos.service.PhotoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.IllegalFormatException;

import static org.springframework.http.ResponseEntity.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@Controller
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @RequestMapping(value = "/{userId}/upload", method = POST)
    public ResponseEntity<String> uploadImage(@PathVariable String userId,
                                              @RequestParam String album,
                                              @RequestParam(value = "file") MultipartFile file) {
        log.info("User: {} uploading photo: {}", userId, file.getOriginalFilename());

        return photoService.uploadImage(userId, album, file.getOriginalFilename(), file) ?
                ok("ok") : badRequest().body("not ok");
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

        byte[] imageBytes = photoService.downloadImage(userId, album, image);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(imageBytes, httpHeaders, HttpStatus.OK);
    }
}
