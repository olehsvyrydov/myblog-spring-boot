package org.javaprojects.myblogsite.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ImageProperties.class)
public class ApplicationConfiguration {
}
