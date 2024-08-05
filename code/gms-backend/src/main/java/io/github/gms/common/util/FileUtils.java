package io.github.gms.common.util;

import io.github.gms.common.types.GmsException;

import static io.github.gms.common.types.ErrorCode.GMS_001;
import static java.util.Objects.isNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class FileUtils {

    private FileUtils() {}

    public static void validatePath(String path) {
        if (isNull(path) || path.contains("../")) {
            throw new GmsException("Could not upload file!", GMS_001);
        }
    }
}
