package org.javaprojects.myblogsite;

import org.javaprojects.myblogsite.services.StorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@DependsOn({"fileSystemStorageService"})
public class WebConfiguration implements WebMvcConfigurer {

    private final StorageService storageService;

    public WebConfiguration(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
        String handler = storageService.getImageDirectory() + "/**";
        String location = storageService.getRootLocation().toString();
        registry.addResourceHandler(handler).addResourceLocations(location);
    }
}
