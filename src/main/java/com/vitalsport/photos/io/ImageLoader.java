package com.vitalsport.photos.io;

import java.io.IOException;

public interface ImageLoader {
    void upload(String path, byte[] bytes) throws IOException;
}
