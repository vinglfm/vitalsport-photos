package com.vitalsport.photos.service;

import com.vitalsport.photos.io.ImageLoader;
import com.vitalsport.photos.validator.InputValidator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static java.lang.String.format;
import static org.mockito.Mockito.*;

public class PhotoServiceTest {

    private static final String userId = "userId";
    private static final String imageAlbum = "imageAlbum";
    private static final String fileName = "fileName";

    private static final String defaultAlbum = "default";
    private static final String path = "imagePath/";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private ImageLoader imageLoader;
    private InputValidator inputValidator;
    private PhotoService photoService;

    @Before
    public void setUp() {
        imageLoader = mock(ImageLoader.class);

        //TODO: think of mock variant
        inputValidator = new InputValidator();
        photoService = new PhotoService(path, defaultAlbum, inputValidator, imageLoader);
    }

    @Test
    public void uploadImageThrowsIllegalArgumentExceptionOnNullUserId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("userId is null or empty.");

        String id = null;
        photoService.uploadImage(id, imageAlbum, fileName, mock(MultipartFile.class));
    }

    @Test
    public void uploadImageThrowsIllegalArgumentExceptionOnEmptyUserId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("userId is null or empty.");

        String id = "";
        photoService.uploadImage(id, imageAlbum, fileName, mock(MultipartFile.class));
    }

    @Test
    public void uploadImageThrowsIllegalArgumentExceptionOnNullFileName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("fileName is null or empty.");

        String file = null;
        photoService.uploadImage(userId, imageAlbum, file, mock(MultipartFile.class));
    }

    @Test
    public void uploadImageThrowsIllegalArgumentExceptionOnEmptyFileName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("fileName is null or empty.");

        String file = "";
        photoService.uploadImage(userId, imageAlbum, file, mock(MultipartFile.class));
    }

    @Test
    public void uploadImageThrowsIllegalArgumentExceptionOnEmptyImage() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Uploading an empty file.");

        MultipartFile emptyMultipartFile = mock(MultipartFile.class);
        when(emptyMultipartFile.isEmpty()).thenReturn(true);

        photoService.uploadImage(userId, imageAlbum, fileName, emptyMultipartFile);
    }

    @Test
    public void uploadImageThrowsIllegalArgumentExceptionWhenMultipartFileIsNotImage() {
        String contentType = "application/pdf";

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(format("ContentType: %s is not supported.", contentType));

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn(contentType);

        photoService.uploadImage(userId, imageAlbum, fileName, multipartFile);
    }

    @Test
    public void uploadImageForValidInputData() throws IOException {
        String contentType = "image/png";
        byte[] expectedBytes = {1, 2, 3};
        String expectedPath = preparePath(userId, imageAlbum, fileName);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getBytes()).thenReturn(expectedBytes);

        photoService.uploadImage(userId, imageAlbum, fileName, multipartFile);
        verify(imageLoader, times(1)).upload(expectedPath, expectedBytes);
    }

    @Test
    public void uploadImageToDefaultAlbumForValidInputDataWithNullAlbum() throws IOException {
        String contentType = "image/png";
        byte[] expectedBytes = {1, 2, 3};
        String album = null;
        String expectedPath = preparePath(userId, defaultAlbum, fileName);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getBytes()).thenReturn(expectedBytes);

        photoService.uploadImage(userId, album, fileName, multipartFile);
        verify(imageLoader, times(1)).upload(expectedPath, expectedBytes);
    }

    @Test
    public void uploadImageToDefaultAlbumForValidInputDataWithEmptyAlbum() throws IOException {
        String contentType = "image/png";
        byte[] expectedBytes = {1, 2, 3};
        String album = "";
        String expectedPath = preparePath(userId, defaultAlbum, fileName);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getBytes()).thenReturn(expectedBytes);

        photoService.uploadImage(userId, album, fileName, multipartFile);
        verify(imageLoader, times(1)).upload(expectedPath, expectedBytes);
    }

    private String preparePath(String userId, String album, String fileName) {
        StringBuilder pathBuilder = new StringBuilder(path);
        pathBuilder.append(userId);
        pathBuilder.append('/');
        pathBuilder.append(album);
        pathBuilder.append('/');
        pathBuilder.append(fileName);
        return pathBuilder.toString();
    }
}