package io.github.gms.secure.service.impl;

import com.google.common.base.Throwables;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.SystemPropertyService;
import io.github.gms.util.TestUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.MDC;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static io.github.gms.common.util.Constants.ENTITY_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@MockitoSettings(strictness = Strictness.LENIENT)
class KeystoreFileServiceImplTest extends AbstractUnitTest {

	private KeystoreRepository repository;
	private UserRepository userRepository;
	private KeystoreFileServiceImpl service;
	private SystemPropertyService systemPropertyService;

	@BeforeEach
	void beforeEach() {
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");

		// Init
		repository = mock(KeystoreRepository.class);
		userRepository = mock(UserRepository.class);
		systemPropertyService = mock(SystemPropertyService.class);
		service = new KeystoreFileServiceImpl(repository, userRepository, "./temp-output/", systemPropertyService);
	}

	@AfterEach
	@SneakyThrows
	public void tearDownAll() {
		TestUtils.deleteDirectoryWithContent("./temp-output");
		MDC.clear();
	}

	@Test
	@SneakyThrows
	void shouldGenerateKeystore() {
		// arrange
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createUser()));
		when(systemPropertyService.get(SystemProperty.ORGANIZATION_NAME)).thenReturn("orgName");
		when(systemPropertyService.get(SystemProperty.ORGANIZATION_CITY)).thenReturn("orgLocation");
		TestUtils.createDirectory("./temp-output/");

		// act & assert
		assertNotNull(service.generate(TestUtils.createSaveKeystoreRequestDto()));
		verify(systemPropertyService).get(SystemProperty.ORGANIZATION_NAME);
		verify(systemPropertyService).get(SystemProperty.ORGANIZATION_CITY);
	}

	@Test
	@SneakyThrows
	void shouldGenerateKeystoreFail() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.generate(dto));

		// assert
		assertEquals(ENTITY_NOT_FOUND, Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@SneakyThrows
	void shouldNotDeleteTempFiles() {
		Path p1 = initMockPath("t/", true, false);
		Path p2 = initMockPath("file1.txt", false, false);
		Path p3 = initMockPath("file2.txt", false, false);
		Path p4 = initMockPath("file3.txt", false, true);

		try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
			mockedStatic.when(() -> Files.list(any(Path.class))).thenReturn(Stream.of(p1, p2, p3, p4));

			when(repository.findByFileName("file1.txt")).thenReturn("file1.txt");
			when(repository.findByFileName("file2.txt")).thenReturn(null);
			when(repository.findByFileName("file3.txt")).thenReturn(null);

			// act
			GmsException response = assertThrows(GmsException.class, () -> service.deleteTempKeystoreFiles());

			// assert
			assertEquals("ERROR!", Throwables.getRootCause(response).getMessage());
		}
	}

	@Test
	@SneakyThrows
	void shouldDeleteTempFiles() {
		Path path1 = mock(Path.class);
		File mockFile1 = mock(File.class);
		when(mockFile1.isDirectory()).thenReturn(false);
		when(mockFile1.getName()).thenReturn("file1.txt");
		when(mockFile1.delete()).thenReturn(true);
		when(path1.toFile()).thenReturn(mockFile1);

		try (MockedStatic<Files> mockedStatic = mockStatic(Files.class)) {
			mockedStatic.when(() -> Files.list(any(Path.class))).thenReturn(Stream.of(path1));

			when(repository.findByFileName("file1.txt")).thenReturn(null);

			// act
			long response = service.deleteTempKeystoreFiles();

			// assert
			assertEquals(1L, response);
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
			when(mockFile1.delete()).thenThrow(new RuntimeException("ERROR!"));
		}
		when(path1.toFile()).thenReturn(mockFile1);

		return path1;
	}
}