package io.github.gms.common.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class FileService {

    public void createDirectories(Path path) throws IOException {
        Files.createDirectories(path);
    }

    public boolean delete(Path path) throws IOException {
        return Files.deleteIfExists(path);
    }

    public boolean exists(Path path) {
        return Files.exists(path);
    }

    public Stream<Path> list(Path path) throws IOException {
        return Files.list(path);
    }

    public byte[] readAllBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    public byte[] toByteArray(File file) throws IOException {
        return readAllBytes(file.toPath());
    }
}
