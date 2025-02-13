package org.javaprojects.myblogsite.services;

import org.springframework.web.multipart.MultipartFile;

import java.net.URI;


public interface StorageService {

    String handleFileUpload(MultipartFile file);

    URI getRootLocation();

    String getImageDirectory();

    String getDefaultFilePath();
}
