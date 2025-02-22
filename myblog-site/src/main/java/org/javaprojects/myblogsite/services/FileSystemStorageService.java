package org.javaprojects.myblogsite.services;

import org.javaprojects.myblogsite.configuration.ImageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * FileSystemStorageService is a Spring service that implements {@link StorageService}
 * to provide file upload storage functionality on the file system.
 * <p>
 * It reads configuration properties from an {@link ImageProperties} bean. The service
 * determines the upload directory from the configured resource and ensures the directory exists.
 * Files uploaded via {@link #handleFileUpload(MultipartFile)} are stored in this directory, and
 * a relative path is returned based on the configured {@code imageDirectory} value.
 * </p>
 *
 * @author Oleh Svyrysov
 */
@Service
public class FileSystemStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(FileSystemStorageService.class);
    private final URI rootLocation;
    private final String imageDirectory;
    private final String defaultFilePath;

    /**
     * Constructs a new {@code FileSystemStorageService} using the provided image properties.
     * <p>
     * The constructor initializes the root upload directory by checking if the configured
     * resource exists and is a file system resource. If the resource represents a file system
     * directory, it creates the directory if it does not already exist. If the resource is not
     * a file, its URI is used directly.
     * </p>
     *
     * @param properties the {@link ImageProperties} containing configuration such as the upload directory location,
     *                   default image path, and upload directory handler path.
     * @throws IllegalStateException if the configured resource does not exist or if an I/O error occurs while creating the directory.
     */
    @Autowired
    public FileSystemStorageService(ImageProperties properties) {
        try {
            Resource resource = properties.uploadDirectoryLocationPath();
            if (!resource.exists()) {
                throw new IllegalStateException(
                        "The configured resource does not exist: " + properties.uploadDirectoryLocationPath());
            }
            if (resource.isFile()) {
                File uploadDir = resource.getFile();
                if (!uploadDir.isDirectory()) {
                    Files.createDirectories(uploadDir.toPath());
                }
                this.rootLocation = uploadDir.toURI();
            } else {
                this.rootLocation = resource.getURI();
            }
        } catch (IOException e) {
            log.error("Error while trying to create the upload directory", e);
            throw new IllegalStateException(e);
        }

        this.defaultFilePath = properties.defaultImagePath();
        this.imageDirectory = Path.of(properties.uploadDirectoryHandlerPath()).toString();
    }

    /**
     * Handles the upload of a file.
     * <p>
     * If the provided {@link MultipartFile} is null or empty, the default file path is returned.
     * Otherwise, the method stores the file in the configured upload directory (specified by {@code rootLocation})
     * and returns a path based on the configured {@code imageDirectory}. The file is stored with its original filename,
     * or if the original filename is not available, a random UUID is used with a ".jpg" extension.
     * </p>
     *
     * @param file the {@link MultipartFile} to upload.
     * @return the relative path to the stored file, or the default file path if the file is null or empty.
     * @throws StorageException if an I/O error occurs during the file storage process.
     */
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

            log.info("Storing file: {}, rootLocation: {}", destinationFileName, rootLocation.getPath());
            log.debug("Properties: imageDirectory: {}, defaultFilePath: {}", imageDirectory, defaultFilePath);
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
