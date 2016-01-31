package com.vitalsport.photos.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
@Service
public class PhotoService implements FileLoader {

    @Value("${photos.path}")
    private String path;

    @Value("${photos.album}")
    private String album;

    @Override
    public boolean loadFile(String userId, String fileName, MultipartFile file) {
        try (OutputStream fileStream = new BufferedOutputStream(
                new FileOutputStream(
                        prepareFile(userId, fileName)))) {
            fileStream.write(file.getBytes());
            log.info("File {} has been successfully uploaded", fileName);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private File prepareFile(String userId, String fileName) {
        File file = new File(preparePath(userId, album, fileName));
        file.getParentFile().mkdirs();
        return file;
    }

    private String preparePath(String userId, String album, String fileName) {
        StringBuilder pathBuilder = new StringBuilder(path);
        pathBuilder.append(userId);
        pathBuilder.append('/');
        pathBuilder.append(album);
        pathBuilder.append(fileName);
        return pathBuilder.toString();
    }

}
