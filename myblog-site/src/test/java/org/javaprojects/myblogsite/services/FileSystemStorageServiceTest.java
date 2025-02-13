package org.javaprojects.myblogsite.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@ActiveProfiles("test")
@SpringBootTest
class FileSystemStorageServiceTest {
    @Autowired
    private  FileSystemStorageService fileSystemStorageService;
    @Test
    void handleFileUpload() throws IOException {
        try (InputStream stream = new ClassPathResource(fileSystemStorageService.getDefaultFilePath()).getInputStream()) {
            assert stream != null;
            MultipartFile multipartFile = new MockMultipartFile("image", "/images/default_image.jpg",
                    MediaType.IMAGE_JPEG_VALUE, stream.readAllBytes());
            String path = fileSystemStorageService.handleFileUpload(multipartFile);
            Assertions.assertNotNull(path);
            Assertions.assertTrue(path.startsWith("/upload/"));
            Assertions.assertTrue(path.endsWith(".jpg"));
        }

    }
}
