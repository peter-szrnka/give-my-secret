package io.github.gms.common.util;

import io.github.gms.common.types.GmsException;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class FileUtils {

    private FileUtils() {}

    public static void validatePath(String path) {
        if (path.startsWith("/") || path.contains("../")) {
            throw new GmsException("Could not upload file!");
        }
    }
}
