package com.vitalsport.photos.service;

import com.vitalsport.photos.io.ImageHandler;
import com.vitalsport.photos.io.PathBuilder;
import com.vitalsport.photos.model.ImageHolder;
import com.vitalsport.photos.validator.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.util.Collection;

import static java.lang.String.format;
import static java.net.URLConnection.guessContentTypeFromName;
import static java.nio.file.Paths.get;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.springframework.http.MediaType.valueOf;

@Slf4j
@Service
public class PhotoService implements PhotoLoader {

    private PathBuilder pathBuilder;
    private Validator validator;
    private ImageHandler imageHandler;

    @Autowired
    public PhotoService(PathBuilder pathBuilder,
                        Validator validator,
                        ImageHandler basicImageHandler) {
        this.pathBuilder = pathBuilder;
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
            imageHandler.upload(pathBuilder.getImagePath(userId, album, fileName),
                    multipartFile.getBytes());
            log.debug("File: {} has been successfully uploaded.", fileName);
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public void createAlbum(String userId, String album) {
        File file = imageHandler.prepareFile(pathBuilder.getAlbumPath(userId, album));

        validator.validate(f -> f.exists(), file,
                format("Album: %s is already exists.", album));
        try {
            file.createNewFile();
        } catch (IOException | SecurityException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public ImageHolder downloadImage(String userId, String album, String image) {

        try {
            return imageHandler.download(pathBuilder.getImagePath(userId, album, image));
        } catch (FileNotFoundException exception) {
            throw new IllegalArgumentException(format("Image: %s wasn't found in album: %s for user: %s.", image, album, userId), exception);
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public void deleteImage(String userId, String album, String image) {
        File file = imageHandler.prepareFile(pathBuilder.getImagePath(userId, album, image));
        validator.validate(f -> !f.exists(), file,
                format("Image: %s doesn't exists in album: %s.", image, album));
        if (!file.delete()) {
            throw new InternalError(format("Unable to delete an image: %s", image));
        }
    }

    @Override
    public void deleteAlbum(String userId, String album) {
        File file = imageHandler.prepareFile(pathBuilder.getAlbumPath(userId, album));
        validator.validate(f -> f.exists(), file,
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
            Path allAlbums = get(pathBuilder.getAllAlbumsPath(userId));
            return imageHandler.getImageInfo(allAlbums,
                    (path) -> path.getFileName().toString());
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public Collection<String> getUserPhotos(String userId, String album) {
        try {
            Path userAlbum = get(pathBuilder.getAlbumPath(userId, album));
            return imageHandler.getImageInfo(userAlbum,
                    (path) -> path.toString());
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }
}
