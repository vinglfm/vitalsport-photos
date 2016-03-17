package com.vitalsport.photos.service;

import com.vitalsport.photos.io.ImageHandler;
import com.vitalsport.photos.builder.DirectoryPathBuilder;
import com.vitalsport.photos.model.ImageHolder;
import com.vitalsport.photos.validator.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.util.Collection;

import static java.lang.String.format;
import static java.nio.file.Paths.get;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.springframework.http.MediaType.valueOf;

@Slf4j
@Service
public class PhotoService implements PhotoLoader {

    private DirectoryPathBuilder directoryPathBuilder;
    private Validator validator;
    private ImageHandler imageHandler;

    @Autowired
    public PhotoService(DirectoryPathBuilder directoryPathBuilder,
                        Validator validator,
                        ImageHandler basicImageHandler) {
        this.directoryPathBuilder = directoryPathBuilder;
        this.validator = validator;
        this.imageHandler = basicImageHandler;
    }

    @Override
    public void uploadImage(String userId, String album, String fileName, MultipartFile multipartFile) {

        validator.validate(StringUtils::isEmpty, userId, "userId is null or empty.");
        validator.validate(StringUtils::isEmpty, fileName, "fileName is null or empty.");
        validator.validate(file -> file.isEmpty(), multipartFile, "Uploading an empty file.");
        validator.validate(file -> !file.getContentType().startsWith("image"), multipartFile,
                format("ContentType: %s is not supported.", multipartFile.getContentType()));

        try {
            imageHandler.upload(directoryPathBuilder.getImagePath(userId, album, fileName),
                    multipartFile.getBytes());
            log.debug("File: {} has been successfully uploaded.", fileName);
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public ImageHolder downloadImage(String userId, String album, String image) {

        validator.validate(StringUtils::isEmpty, userId, "userId is null or empty.");
        validator.validate(StringUtils::isEmpty, album, "album is null or empty.");
        validator.validate(StringUtils::isEmpty, image, "fileName is null or empty.");

        try {
            return imageHandler.download(directoryPathBuilder.getImagePath(userId, album, image));
        } catch (FileNotFoundException exception) {
            throw new IllegalArgumentException(format("Image: %s wasn't found in album: %s for user: %s.", image, album, userId), exception);
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public void deleteImage(String userId, String album, String image) {

        validator.validate(StringUtils::isEmpty, userId, "userId is null or empty.");
        validator.validate(StringUtils::isEmpty, album, "album is null or empty.");
        validator.validate(StringUtils::isEmpty, image, "fileName is null or empty.");

        File file = imageHandler.prepareFile(directoryPathBuilder.getImagePath(userId, album, image));
        file.delete();
    }

    @Override
    public void createAlbum(String userId, String album) {
        File file = imageHandler.prepareFile(directoryPathBuilder.getAlbumPath(userId, album));

        validator.validate(f -> f.exists(), file,
                format("Album: %s is already exists.", album));
        try {
            //TODO: create a userId folder if it doesn't exist
            file.mkdir();
        } catch (SecurityException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public void deleteAlbum(String userId, String album) {
        File file = imageHandler.prepareFile(directoryPathBuilder.getAlbumPath(userId, album));
        validator.validate(f -> !f.exists(), file,
                format("Album: %s does not exists.", album));
        try {
            deleteDirectory(file);
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public Collection<String> getUserAlbums(String userId) {
        try {
            Path allAlbums = get(directoryPathBuilder.getAllAlbumsPath(userId));
            return imageHandler.getImageInfo(allAlbums,
                    (path) -> path.getFileName().toString());
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public Collection<String> getUserPhotos(String userId, String album) {
        try {
            Path userAlbum = get(directoryPathBuilder.getAlbumPath(userId, album));
            return imageHandler.getImageInfo(userAlbum,
                    (path) -> path.getFileName().toString());
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }
}
