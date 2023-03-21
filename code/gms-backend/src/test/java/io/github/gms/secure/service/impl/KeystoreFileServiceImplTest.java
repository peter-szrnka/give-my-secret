package io.github.gms.secure.service.impl;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.repository.KeystoreRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@MockitoSettings(strictness = Strictness.LENIENT)
class KeystoreFileServiceImplTest extends AbstractUnitTest {

	@Mock
	private KeystoreRepository repository;

	@InjectMocks
	private KeystoreFileServiceImpl service;

	@BeforeEach
	void beforeEach() {
		ReflectionTestUtils.setField(service, "keystoreTempPath", "./temp-unit-test/");
	}

	@Test
	void shouldGenerateKeystore() {
		assertEquals("generated.jks", service.generate(new SaveKeystoreRequestDto()));
	}

	@Test
	@SneakyThrows
	void shouldDeleteTempFiles() {
		Path p1 = initMockPath("t/", true, false);
		Path p2 = initMockPath("file1.txt", false, false);
		Path p3 = initMockPath("file2.txt", false, false);
		Path p4 = initMockPath("file3.txt", false, true);

		try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
			mockedStatic.when(() -> Files.list(any(Path.class))).thenReturn(Stream.of(p1, p2, p3, p4));

			when(repository.findByFileName(eq("file1.txt"))).thenReturn("file1.txt");
			when(repository.findByFileName(eq("file2.txt"))).thenReturn(null);
			when(repository.findByFileName(eq("file3.txt"))).thenReturn(null);

			// act
			GmsException response = assertThrows(GmsException.class, () -> service.deleteTempKeystoreFiles());

			// assert
			assertEquals("java.lang.RuntimeException: java.lang.RuntimeException", response.getMessage());
		}
	}

	private Path initMockPath(String fileName, boolean isDirectory, boolean throwError) {
		Path path1 = mock(Path.class);
		File mockFile1 = mock(File.class);
		when(mockFile1.isDirectory()).thenReturn(isDirectory);

		if (!isDirectory) {
			when(mockFile1.getName()).thenReturn(fileName);

		}

		if (throwError) {
			when(mockFile1.delete()).thenThrow(RuntimeException.class);
		}
		when(path1.toFile()).thenReturn(mockFile1);

		return path1;
	}
}