package io.github.gms.common.util;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.types.GmsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test of {@link FileUtils}
 */
class FileUtilsTest extends AbstractUnitTest {

    @Test
    void shouldTestPrivateConstructor() {
        assertPrivateConstructor(FileUtils.class);
    }

    @ParameterizedTest
    @MethodSource("testData")
    void shouldFail(String input) {
        assertThrows(GmsException.class, () -> FileUtils.validatePath(input));
    }

    @Test
    void shouldPass() {
        assertDoesNotThrow(() -> FileUtils.validatePath("correctpath/correctfile.txt"));
    }

    private static String[] testData() {
        return new String[] {
                null,
                "hack/../../root/etc/password",
                "/root/etc/password"
        };
    }
}
