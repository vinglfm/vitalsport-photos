package com.vitalsport.photos.io;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PathBuilder {

    private static final char DELIMITER = '/';

    private String path;
    private String defaultAlbum;

    @Autowired
    public PathBuilder(@Value("${photos.path}") String path,
                       @Value("${photos.defaultAlbum}") String defaultAlbum) {
        this.path = path;
        this.defaultAlbum = defaultAlbum;
    }

    public String getAllAlbumsPath(String userId) {
        return allAlbumsPath(userId).toString();
    }

    public String getAlbumPath(String userId, String album) {
        StringBuilder pathBuilder = allAlbumsPath(userId);
        append(pathBuilder, retrieveAlbum(album));
        return pathBuilder.toString();
    }

    public String getImagePath(String userId, String album, String fileName) {
        StringBuilder pathBuilder = allAlbumsPath(userId);
        append(pathBuilder, retrieveAlbum(album));
        append(pathBuilder, fileName);
        return pathBuilder.toString();
    }

    private String retrieveAlbum(String album) {
        return StringUtils.isEmpty(album) ? defaultAlbum : album;
    }

    private void append(StringBuilder builder, String name) {
        builder.append(DELIMITER);
        builder.append(name);
    }

    private StringBuilder allAlbumsPath(String userId) {
        StringBuilder pathBuilder = new StringBuilder(path);
        pathBuilder.append(userId);
        return pathBuilder;
    }

}
