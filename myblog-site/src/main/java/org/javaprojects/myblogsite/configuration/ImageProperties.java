package org.javaprojects.myblogsite.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public record ImageProperties(
        @Value("${application.images.upload-directory-location-path}")
        Resource uploadResource,
        @Value("${application.images.default-image-path:/images/default_image.jpg}")
        String defaultImagePath,
        @Value("${application.images.upload-directory-handler-path:/images}")
        String imageDirectory
) {}
