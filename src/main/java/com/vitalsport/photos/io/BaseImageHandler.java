package com.vitalsport.photos.io;

import com.vitalsport.photos.model.ImageHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import static java.net.URLConnection.guessContentTypeFromName;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.springframework.http.MediaType.valueOf;

@Component
public class BaseImageHandler implements ImageHandler {

    @Override
    public void upload(String path, byte[] bytes) throws IOException {
        File imageFile = createFile(path);
        try (OutputStream fileStream = new BufferedOutputStream(
                new FileOutputStream(imageFile))) {
            fileStream.write(bytes);
        }
    }

    @Override
    public ImageHolder download(String path) throws IOException {
        File file = prepareFile(path);
        MediaType mimeType = getMimeType(file);
        try (InputStream input = new FileInputStream(file)) {
            return new ImageHolder(mimeType, toByteArray(input));
        }
    }

    @Override
    public Collection<String> getImageInfo(Path userFolder, Function<Path, String> infoFunction) throws IOException {
        Collection<String> photos = new ArrayList<>();

        if (Files.exists(userFolder)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(userFolder)) {
                for (Path path : directoryStream) {
                    photos.add(infoFunction.apply(path));
                }
            }
        }

        return photos;
    }

    @Override
    public File prepareFile(String path) {
        return new File(path);
    }


    private MediaType getMimeType(File file) throws IOException {
        return valueOf(guessContentTypeFromName(file.getName()));
    }

    private File createFile(String path) {
        File file = prepareFile(path);
        file.getParentFile().mkdirs();
        return file;
    }
}
