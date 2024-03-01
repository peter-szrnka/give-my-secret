package io.github.gms.common.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface FileService {

    void createDirectories(Path path) throws IOException;

    boolean delete(Path path) throws IOException;

    boolean exists(Path path);

    Stream<Path> list(Path path) throws IOException;

    byte[] readAllBytes(Path path) throws IOException;

    byte[] toByteArray(File file) throws IOException;
}
