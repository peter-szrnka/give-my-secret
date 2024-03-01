package io.github.gms.common.service.impl;

import io.github.gms.common.service.FileService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Default implementation of file services
 *
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class FileServiceImpl implements FileService {

    @Override
    public void createDirectories(Path path) throws IOException {
        Files.createDirectories(path);
    }

    @Override
    public boolean delete(Path path) throws IOException {
        return Files.deleteIfExists(path);
    }

    @Override
    public boolean exists(Path path) {
        return Files.exists(path);
    }

    @Override
    public Stream<Path> list(Path path) throws IOException {
        return Files.list(path);
    }

    @Override
    public byte[] readAllBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    @Override
    public byte[] toByteArray(File file) throws IOException {
        return readAllBytes(file.toPath());
    }
}