package io.github.stackpan.tasque.config;

import io.github.stackpan.tasque.config.properties.StorageConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    private final StorageConfigProperties storageConfigProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(storageConfigProperties.localHandler() + "/**").addResourceLocations("file:" + storageConfigProperties.localLocation());
    }
}
