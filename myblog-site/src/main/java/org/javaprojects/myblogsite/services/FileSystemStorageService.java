package org.javaprojects.myblogsite.services;

import org.javaprojects.myblogsite.configuration.ImageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Configuration
public class FileSystemStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(FileSystemStorageService.class);
    private final URI rootLocation;
    private final String imageDirectory;
    private final String defaultFilePath;

    @Autowired
    public FileSystemStorageService(ImageProperties properties) {
        try {
            Resource resource = properties.uploadResource();
            if (!resource.exists()) {
                throw new IllegalStateException(
                        "The configured resource does not exist: " + properties.uploadResource());
            }
            this.rootLocation = resource.getURI();
        } catch (IOException e) {
            log.error("Error while trying to create the upload directory", e);
            throw new IllegalStateException(e);
        }

        this.defaultFilePath = properties.defaultImagePath();
        this.imageDirectory = Path.of(properties.imageDirectory()).toString();
    }

    @Override
    public String handleFileUpload(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                log.warn("Failed to store empty file or it was not uploaded.");
                return defaultFilePath;
            }
            log.info("Storing file: Original name: {}, name: {}, contentType: {}", file.getOriginalFilename(), file.getName(), file.getContentType());
            String destinationFileName;
            String filename = file.getOriginalFilename();
            if (filename != null && !filename.isEmpty()) {
                destinationFileName = Path.of(filename).getFileName().toString();
            } else {
                destinationFileName = UUID.randomUUID() + ".jpg";
            }

            log.info("Storing file: {}", destinationFileName);
            try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
                Files.copy(inputStream, Paths.get(rootLocation.getPath(), destinationFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            Path path = Paths.get(imageDirectory, destinationFileName);
            return path.toString();

        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    public URI getRootLocation() {
        return rootLocation;
    }

    public String getImageDirectory() {
        return imageDirectory;
    }

    public String getDefaultFilePath() {
        return defaultFilePath;
    }
}
