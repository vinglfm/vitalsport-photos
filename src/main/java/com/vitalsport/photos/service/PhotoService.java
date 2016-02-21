package com.vitalsport.photos.service;

import com.vitalsport.photos.io.ImageLoader;
import com.vitalsport.photos.model.ImageHolder;
import com.vitalsport.photos.validator.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import static java.lang.String.format;
import static java.net.URLConnection.guessContentTypeFromName;

@Slf4j
@Service
public class PhotoService implements PhotoLoader {

    private String path;
    private String defaultAlbum;
    private Validator validator;
    private ImageLoader imageLoader;

    @Autowired
    public PhotoService(@Value("${photos.path}") String path,
                        @Value("${photos.defaultAlbum}") String defaultAlbum,
                        Validator validator,
                        ImageLoader basicImageLoader) {
        this.path = path;
        this.defaultAlbum = defaultAlbum;
        this.validator = validator;
        this.imageLoader = basicImageLoader;
    }

    @Override
    public void uploadImage(String userId, String album, String fileName, MultipartFile multipartFile) {

        validator.validate(StringUtils::isEmpty, userId, "userId is null or empty.");
        validator.validate(StringUtils::isEmpty, fileName, "fileName is null or empty.");
        validator.validate(file -> file.isEmpty(), multipartFile, "Uploading an empty file.");
        validator.validate(file -> !file.getContentType().startsWith("image"), multipartFile,
                format("ContentType: %s is not supported.", multipartFile.getContentType()));

        String uploadingPath = preparePath(userId, retrieveAlbum(album), fileName);
        try {
            imageLoader.upload(uploadingPath, multipartFile.getBytes());
            log.debug("File: {} has been successfully uploaded.", fileName);
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }

    private String retrieveAlbum(String album) {
        return StringUtils.isEmpty(album) ? defaultAlbum : album;
    }

    @Override
    public void createAlbum(String userId, String album) {
        File file = new File(preparePath(userId, album));

        validator.validate(f -> f.exists(), file,
                format("Album: %s is already exists.", album));
        try {
            file.createNewFile();
        } catch (IOException | SecurityException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public ImageHolder downloadImage(String userId, String album, String image) throws IOException {
        File file = new File(preparePath(userId, album, image));
        try (InputStream input = new FileInputStream(file)) {
            return new ImageHolder(getMimeType(file), IOUtils.toByteArray(input));
        } catch (FileNotFoundException exception) {
            throw new IllegalArgumentException(format("Image: %s wasn't found in album: %s for user: %s.", image, album, userId), exception);
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public void deleteImage(String userId, String album, String image) {
        File file = new File(preparePath(userId, album, image));
        validator.validate(f -> !f.exists(), file,
                format("Image: %s doesn't exists in album: %s.", image, album));
        if (!file.delete()) {
            throw new InternalError(format("Unable to delete image: %s", image));
        }
    }

    @Override
    public void deleteAlbum(String userId, String album) {
        File file = new File(preparePath(userId, album));
        validator.validate(f -> f.exists(), file,
                format("Album: %s does not exists.", album));
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }

    private MediaType getMimeType(File file) throws IOException {
        return MediaType.valueOf(guessContentTypeFromName(file.getName()));
    }

    @Override
    public Collection<String> getUserAlbums(String userId) {
        return getUserData(Paths.get(preparePath(userId)),
                (path) -> path.getFileName().toString());
    }

    @Override
    public Collection<String> getUserPhotos(String userId, String album) {
        return getUserData(Paths.get(preparePath(userId, album)),
                (path) -> path.toString());
    }

    private Collection<String> getUserData(Path userFolder, Function<Path, String> dataFunction) {
        Collection<String> photos = new ArrayList<>();

        if (Files.exists(userFolder)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(userFolder)) {
                for (Path path : directoryStream) {
                    photos.add(dataFunction.apply(path));
                }
            } catch (IOException exception) {
                throw new InternalError(exception);
            }
        }

        return photos;
    }

    //TODO: refactor path creation, move to PathBuilder
    private String preparePath(String userId, String album, String fileName) {
        StringBuilder pathBuilder = new StringBuilder(path);
        pathBuilder.append(userId);
        pathBuilder.append('/');
        pathBuilder.append(album);
        pathBuilder.append('/');
        pathBuilder.append(fileName);
        return pathBuilder.toString();
    }

    private String preparePath(String userId) {
        StringBuilder pathBuilder = new StringBuilder(path);
        pathBuilder.append(userId);
        return pathBuilder.toString();
    }

    private String preparePath(String userId, String album) {
        StringBuilder pathBuilder = new StringBuilder(path);
        pathBuilder.append(userId);
        pathBuilder.append('/');
        pathBuilder.append(album);
        return pathBuilder.toString();
    }

}
