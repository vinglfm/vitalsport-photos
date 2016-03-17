package com.vitalsport.photos.service;

import com.vitalsport.photos.io.ImageHandler;
import com.vitalsport.photos.builder.DirectoryPathBuilder;
import com.vitalsport.photos.model.ImageHolder;
import com.vitalsport.photos.validator.InputValidator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class PhotoServiceTest {

    private static final String userId = "userId";
    private static final String imageAlbum = "imageAlbum";
    private static final String fileName = "fileName";

    private static final String defaultAlbum = "default";
    private static final String path = "imagePath/";

    private ImageHandler imageHandler;
    private InputValidator inputValidator;
    private PhotoService photoService;
    private DirectoryPathBuilder directoryPathBuilder;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        imageHandler = mock(ImageHandler.class);
        inputValidator = new InputValidator();
        directoryPathBuilder = new DirectoryPathBuilder(path, defaultAlbum);
        photoService = new PhotoService(directoryPathBuilder, inputValidator, imageHandler);
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
        String expectedPath = directoryPathBuilder.getImagePath(userId, imageAlbum, fileName);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getBytes()).thenReturn(expectedBytes);

        photoService.uploadImage(userId, imageAlbum, fileName, multipartFile);
        verify(imageHandler, times(1)).upload(expectedPath, expectedBytes);
    }

    @Test
    public void uploadImageToDefaultAlbumForValidInputDataWithNullAlbum() throws IOException {
        String contentType = "image/png";
        byte[] expectedBytes = {1, 2, 3};
        String album = null;
        String expectedPath = directoryPathBuilder.getImagePath(userId, defaultAlbum, fileName);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getBytes()).thenReturn(expectedBytes);

        photoService.uploadImage(userId, album, fileName, multipartFile);
        verify(imageHandler, times(1)).upload(expectedPath, expectedBytes);
    }

    @Test
    public void uploadImageToDefaultAlbumForValidInputDataWithEmptyAlbum() throws IOException {
        String contentType = "image/png";
        byte[] expectedBytes = {1, 2, 3};
        String album = "";
        String expectedPath = directoryPathBuilder.getImagePath(userId, defaultAlbum, fileName);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getBytes()).thenReturn(expectedBytes);

        photoService.uploadImage(userId, album, fileName, multipartFile);
        verify(imageHandler, times(1)).upload(expectedPath, expectedBytes);
    }

    @Test
    public void downloadImageThrowsIllegalArgumentExceptionOnNullUserId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("userId is null or empty.");

        String id = null;
        photoService.downloadImage(id, imageAlbum, fileName);
    }

    @Test
    public void downloadImageThrowsIllegalArgumentExceptionOnEmptyUserId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("userId is null or empty.");

        String id = "";
        photoService.downloadImage(id, imageAlbum, fileName);
    }

    @Test
    public void downloadImageThrowsIllegalArgumentExceptionOnNullAlbum() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("album is null or empty.");

        String album = null;
        photoService.downloadImage(userId, album, fileName);
    }

    @Test
    public void downloadImageThrowsIllegalArgumentExceptionOnEmptyAlbum() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("album is null or empty.");

        String album = "";
        photoService.downloadImage(userId, album, fileName);
    }

    @Test
    public void downloadImageThrowsIllegalArgumentExceptionOnNullFileName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("fileName is null or empty.");

        String file = null;
        photoService.downloadImage(userId, imageAlbum, file);
    }

    @Test
    public void downloadImageThrowsIllegalArgumentExceptionOnEmptyFileName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("fileName is null or empty.");

        String file = "";
        photoService.downloadImage(userId, imageAlbum, file);
    }

    @Test
    public void downloadImageThrowsIllegalArgumentExceptionWhenImageNotFound() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Image: fileName wasn't found in album: imageAlbum for user: userId.");

        when(imageHandler.download(anyString())).thenThrow(FileNotFoundException.class);

        photoService.downloadImage(userId, imageAlbum, fileName);
    }

    @Test
    public void downloadImageThrowsInternalErrorWhenIOExceptionHasBeenThrown() throws IOException {
        expectedException.expect(InternalError.class);

        when(imageHandler.download(anyString())).thenThrow(IOException.class);

        photoService.downloadImage(userId, imageAlbum, fileName);
    }

    @Test
    public void downloadImageForValidInputData() throws IOException {
        String pathToFile = directoryPathBuilder.getImagePath(userId, imageAlbum, fileName);
        ImageHolder expectedResult = mock(ImageHolder.class);
        when(imageHandler.download(pathToFile)).thenReturn(expectedResult);

        assertThat(photoService.downloadImage(userId, imageAlbum, fileName)).isEqualTo(expectedResult);
        verify(imageHandler, times(1)).download(pathToFile);
    }

    @Test
    public void deleteImageThrowsIllegalArgumentExceptionOnNullUserId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("userId is null or empty.");

        String id = null;
        photoService.deleteImage(id, imageAlbum, fileName);
    }

    @Test
    public void deleteImageThrowsIllegalArgumentExceptionOnEmptyUserId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("userId is null or empty.");

        String id = "";
        photoService.deleteImage(id, imageAlbum, fileName);
    }

    @Test
    public void deleteImageThrowsIllegalArgumentExceptionOnNullAlbum() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("album is null or empty.");

        String album = null;
        photoService.deleteImage(userId, album, fileName);
    }

    @Test
    public void deleteImageThrowsIllegalArgumentExceptionOnEmptyAlbum() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("album is null or empty.");

        String album = "";
        photoService.deleteImage(userId, album, fileName);
    }

    @Test
    public void deleteImageThrowsIllegalArgumentExceptionOnNullFileName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("fileName is null or empty.");

        String file = null;
        photoService.deleteImage(userId, imageAlbum, file);
    }

    @Test
    public void deleteImageThrowsIllegalArgumentExceptionOnEmptyFileName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("fileName is null or empty.");

        String file = "";
        photoService.deleteImage(userId, imageAlbum, file);
    }

    @Test
    public void deleteImageOnValidInputData() {
        String pathToFile = directoryPathBuilder.getImagePath(userId, imageAlbum, fileName);
        File file = mock(File.class);
        when(imageHandler.prepareFile(pathToFile)).thenReturn(file);

        photoService.deleteImage(userId, imageAlbum, fileName);
        verify(file, times(1)).delete();
    }

}