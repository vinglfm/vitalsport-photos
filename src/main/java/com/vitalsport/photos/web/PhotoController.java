package com.vitalsport.photos.web;

import com.vitalsport.photos.service.PhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@Controller
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    //TODO: add meta data like album name
    @RequestMapping(value = "/{userId}/upload/{fileName}", method = POST)
    public ResponseEntity<String> uploadPhoto(@PathVariable String userId, @PathVariable String fileName,
                                              @RequestParam(value = "file") MultipartFile file) {
        log.debug("User: {} uploading photo: {}", userId, fileName);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        return photoService.loadFile(userId, fileName, file) ?
                ResponseEntity.ok("ok") : ResponseEntity.badRequest().body("not ok");
    }

}
