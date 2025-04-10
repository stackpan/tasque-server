package io.github.stackpan.tasque.fs;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@NoArgsConstructor
public class LocalFileStorage implements FileStorage {

    private String storagePath = "uploads/";

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Path.of(storagePath));
    }

    @Value("${storage.path:uploads/}")
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public void save(String filename, byte[] bytes) throws IOException {
        var path = Paths.get(storagePath, filename);
        Files.write(path, bytes);
    }

    @Override
    public void delete(String filename) throws IOException {
        var path = Paths.get(storagePath, filename);
        Files.deleteIfExists(path);
    }
}
