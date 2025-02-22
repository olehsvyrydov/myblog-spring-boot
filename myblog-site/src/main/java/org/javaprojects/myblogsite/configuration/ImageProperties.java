package org.javaprojects.myblogsite.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "application.images")
public record ImageProperties(
        Resource uploadDirectoryLocationPath,
        String defaultImagePath,
        String uploadDirectoryHandlerPath
) {}
