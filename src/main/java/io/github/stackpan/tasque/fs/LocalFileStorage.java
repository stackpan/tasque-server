package io.github.stackpan.tasque.fs;

import io.github.stackpan.tasque.config.properties.StorageConfigProperties;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@NoArgsConstructor
public class LocalFileStorage implements FileStorage {

    private StorageConfigProperties storageConfigProperties;

    @Autowired
    public void setStorageConfigProperties(StorageConfigProperties storageConfigProperties) {
        this.storageConfigProperties = storageConfigProperties;
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Path.of(storageConfigProperties.localLocation()));
    }

    @Override
    public void save(String filename, byte[] bytes) throws IOException {
        var path = Paths.get(storageConfigProperties.localLocation(), filename);
        Files.write(path, bytes);
    }

    @Override
    public void delete(String filename) throws IOException {
        var path = Paths.get(storageConfigProperties.localLocation(), filename);
        Files.deleteIfExists(path);
    }
}
