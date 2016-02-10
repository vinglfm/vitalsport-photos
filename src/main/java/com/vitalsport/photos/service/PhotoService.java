package com.vitalsport.photos.service;

import com.vitalsport.photos.validator.UploadValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import static java.lang.String.format;

@Slf4j
@Service
public class PhotoService implements PhotoLoader {

    @Value("${photos.path}")
    private String path;

    @Autowired
    private UploadValidator<MultipartFile> uploadValidator;

    @Override
    public boolean uploadImage(String userId, String album, String fileName, MultipartFile multipartFile) {

        uploadValidator.validate(multipartFile);

        File imageFile = createFile(userId, album, fileName);
        try (OutputStream fileStream = new BufferedOutputStream(
                new FileOutputStream(
                        imageFile))) {
            fileStream.write(multipartFile.getBytes());
            log.info("File {} has been successfully uploaded", fileName);
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    @Override
    public byte[] downloadImage(String userId, String album, String image) throws IOException {
        try (InputStream input = new FileInputStream(new File(prepareFilePath(userId, album, image)))) {
            return IOUtils.toByteArray(input);
        } catch (FileNotFoundException exception) {
            throw new IllegalArgumentException(format("Image: %s wasn't found in album: %s for user: %s", image, album, userId), exception);
        } catch (IOException exception) {
            throw new InternalError(exception);
        }
    }

    @Override
    public Collection<String> getUserAlbums(String userId) {
        return getUserData(Paths.get(prepareUserPath(userId)),
                (path) -> path.getFileName().toString());
    }

    @Override
    public Collection<String> getUserPhotos(String userId, String album) {
        return getUserData(Paths.get(prepareUserPath(userId, album)),
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
        return pathBuilder.toString();
    }

    private String prepareUserPath(String userId, String album) {
        StringBuilder pathBuilder = new StringBuilder(path);
        pathBuilder.append(userId);
        pathBuilder.append('/');
        pathBuilder.append(album);
        return pathBuilder.toString();
    }

}
