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
    void shouldCreateDirectories() {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            Path path = mock(Path.class);
            mockedStatic.when(() -> Files.createDirectories(any(Path.class))).thenReturn(path);

            // act
            service.createDirectories(path);

            // assert
            mockedStatic.verify(() -> Files.createDirectories(any(Path.class)));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldDelete(boolean mockResult) {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            Path path = mock(Path.class);
            mockedStatic.when(() -> Files.deleteIfExists(any(Path.class))).thenReturn(mockResult);

            // act
            boolean response = assertDoesNotThrow(() -> service.delete(path));

            // assert
            assertEquals(mockResult, response);
            mockedStatic.verify(() -> Files.deleteIfExists(any(Path.class)));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldExists(boolean mockResult) {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            Path path = mock(Path.class);
            mockedStatic.when(() -> Files.exists(any(Path.class))).thenReturn(mockResult);

            // act
            boolean response =  assertDoesNotThrow(() -> service.exists(path));

            // assert
            assertEquals(mockResult, response);

            ArgumentCaptor<Path> argumentCaptor = ArgumentCaptor.forClass(Path.class);
            mockedStatic.verify(() -> Files.exists(argumentCaptor.capture()));
            assertEquals(path, argumentCaptor.getValue());
        }
    }

    @Test
    @SneakyThrows
    void shouldList() {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            Path path = mock(Path.class);
            Stream<Path> mockStream = Stream.of(path);
            mockedStatic.when(() -> Files.list(any(Path.class))).thenReturn(mockStream);

            // act
            Stream<Path> response =  service.list(path);

            // assert
            assertNotNull(response);
            assertEquals(response, mockStream);
            mockedStatic.verify(() -> Files.list(any(Path.class)));
        }
    }

    @Test
    @SneakyThrows
    void shouldReadAllBytes() {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            Path path = mock(Path.class);
            mockedStatic.when(() -> Files.readAllBytes(any(Path.class))).thenReturn("data".getBytes());

            // act
            byte[] response =  service.readAllBytes(path);

            // assert
            assertNotNull(response);
            assertEquals("data", new String(response));
            mockedStatic.verify(() -> Files.readAllBytes(any(Path.class)));
        }
    }

    @Test
    @SneakyThrows
    void shouldReadToByteArray() {
        try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
            File mockFile = mock(File.class);
            Path path = mock(Path.class);
            when(mockFile.toPath()).thenReturn(path);
            mockedStatic.when(() -> Files.readAllBytes(any(Path.class))).thenReturn("data".getBytes());

            // act
            byte[] response =  service.toByteArray(mockFile);

            // assert
            assertNotNull(response);
            assertEquals("data", new String(response));
            mockedStatic.verify(() -> Files.readAllBytes(any(Path.class)));
        }
    }
}
