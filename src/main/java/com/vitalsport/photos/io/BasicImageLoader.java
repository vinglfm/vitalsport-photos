package com.vitalsport.photos.io;

import java.io.*;

public class BasicImageLoader implements ImageLoader {

    @Override
    public void upload(String path, byte[] bytes) throws IOException {
        File imageFile = createFile(path);
        try (OutputStream fileStream = new BufferedOutputStream(
                new FileOutputStream(
                        imageFile))) {
            fileStream.write(bytes);
        }
    }

    private File createFile(String path) {
        File file = new File(path);
        file.getParentFile().mkdirs();
        return file;
    }
}
