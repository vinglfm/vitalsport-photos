package com.vitalsport.photos.io;

import com.vitalsport.photos.model.ImageHolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;

public interface ImageHandler {
    void upload(String path, byte[] bytes) throws IOException;

    ImageHolder download(String path) throws IOException;

    Collection<String> getImageInfo(Path userFolder, Function<Path, String> dataFunction) throws IOException;

    File prepareFile(String path);
}
