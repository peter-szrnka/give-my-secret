package io.github.gms.common.service;

import io.github.gms.abstraction.AbstractUnitTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class FileServiceTest extends AbstractUnitTest {

    private final FileService service = new FileService();

    @Test
    @SneakyThrows
    void createDirectories_whenPathIsValid_thenCreateDirectories() {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            Path path = mock(Path.class);
            mockedStatic.when(() -> Files.createDirectories(any(Path.class))).thenReturn(path);

            // when
            service.createDirectories(path);

            // then
            mockedStatic.verify(() -> Files.createDirectories(any(Path.class)));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void delete_whenFileExistsOrNot_thenReturnResult(boolean mockResult) {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            Path path = mock(Path.class);
            mockedStatic.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(mockResult);

            // when
            boolean response = assertDoesNotThrow(() -> service.delete(path));

            // then
            assertEquals(mockResult, response);
            mockedStatic.verify(() -> Files.deleteIfExists(any(Path.class)));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void exists_whenPathIsValid_thenReturnResult(boolean mockResult) {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            Path path = mock(Path.class);
            mockedStatic.when(() -> Files.exists(any(Path.class))).thenReturn(mockResult);

            // when
            boolean response =  assertDoesNotThrow(() -> service.exists(path));

            // then
            assertEquals(mockResult, response);

            ArgumentCaptor<Path> argumentCaptor = ArgumentCaptor.forClass(Path.class);
            mockedStatic.verify(() -> Files.exists(argumentCaptor.capture()));
            assertEquals(path, argumentCaptor.getValue());
        }
    }

    @Test
    @SneakyThrows
    void list_whenPathIsValid_thenReturnList() {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            Path path = mock(Path.class);
            Stream<Path> mockStream = Stream.of(path);
            mockedStatic.when(() -> Files.list(any(Path.class))).thenReturn(mockStream);

            // when
            Stream<Path> response =  service.list(path);

            // then
            assertNotNull(response);
            assertEquals(response, mockStream);
            mockedStatic.verify(() -> Files.list(any(Path.class)));
        }
    }

    @Test
    @SneakyThrows
    void readAllBytes_whenInputFileIsValid_thenReturnBytes() {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            Path path = mock(Path.class);
            mockedStatic.when(() -> Files.readAllBytes(any(Path.class))).thenReturn("data".getBytes());

            // when
            byte[] response =  service.readAllBytes(path);

            // then
            assertNotNull(response);
            assertEquals("data", new String(response));
            mockedStatic.verify(() -> Files.readAllBytes(any(Path.class)));
        }
    }

    @Test
    @SneakyThrows
    void toByteArray_whenInputFileIsValid_thenReturnBytes() {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            File mockFile = mock(File.class);
            Path path = mock(Path.class);
            when(mockFile.toPath()).thenReturn(path);
            mockedStatic.when(() -> Files.readAllBytes(any(Path.class))).thenReturn("data".getBytes());

            // when
            byte[] response =  service.toByteArray(mockFile);

            // then
            assertNotNull(response);
            assertEquals("data", new String(response));
            mockedStatic.verify(() -> Files.readAllBytes(any(Path.class)));
        }
    }
}

