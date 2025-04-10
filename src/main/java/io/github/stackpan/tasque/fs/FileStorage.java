package io.github.stackpan.tasque.fs;

import java.io.IOException;

public interface FileStorage {

    void save(String filename, byte[] bytes) throws IOException;

    void delete(String filename) throws IOException;

}
