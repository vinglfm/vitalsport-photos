package com.vitalsport.photos.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
public class PhotoService implements PhotoLoader {

    @Value("${photos.path}")
    private String path;

    @Override
    public boolean loadPhoto(String userId, String album, String fileName, MultipartFile multipartFile) {
        File imageFile = createFile(userId, album, fileName);
        try (OutputStream fileStream = new BufferedOutputStream(
                new FileOutputStream(
                        imageFile))) {
            fileStream.write(multipartFile.getBytes());
            log.info("File {} has been successfully uploaded", fileName);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Collection<String> getUserAlbums(String userId) {

        Collection<String> albums = new ArrayList<>();

        Path userFolder = Paths.get(prepareUserPath(userId));
        if(Files.exists(userFolder)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(userFolder)) {
                for (Path path : directoryStream) {
                    albums.add(path.getFileName().toString());
                }
            } catch (IOException exception) {
                throw new InternalError(exception);
            }
        }

        return albums;
    }

    private File createFile(String userId, String album, String fileName) {
        File file = new File(prepareFilePath(userId, album, fileName));
        file.getParentFile().mkdirs();
        return file;
    }

    //TODO: refactor path creation
    private String prepareFilePath(String userId, String album, String fileName) {
        StringBuilder pathBuilder = new StringBuilder(path);
        pathBuilder.append(userId);
        pathBuilder.append('/');
        pathBuilder.append(album);
        pathBuilder.append('/');
        pathBuilder.append(fileName);
        return pathBuilder.toString();
    }

    private String prepareUserPath(String userId) {
        StringBuilder pathBuilder = new StringBuilder(path);
        pathBuilder.append(userId);
        pathBuilder.append('/');
        return pathBuilder.toString();
    }

}
