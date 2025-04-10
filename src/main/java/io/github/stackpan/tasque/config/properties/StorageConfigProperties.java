package io.github.stackpan.tasque.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
public record StorageConfigProperties(
        String localLocation,
        String localHandler
) {
}
