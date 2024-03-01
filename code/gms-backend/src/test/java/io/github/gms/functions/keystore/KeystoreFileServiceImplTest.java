package io.github.gms.functions.keystore;

import com.google.common.base.Throwables;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.service.FileService;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.TestUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.MDC;

import java.io.File;
import java.io.IOException;
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
import static org.mockito.Mockito.times;
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
    private FileService fileService;

	@BeforeEach
	void beforeEach() {
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");

		// Init
		repository = mock(KeystoreRepository.class);
		userRepository = mock(UserRepository.class);
		systemPropertyService = mock(SystemPropertyService.class);
        fileService = mock(FileService.class);
		service = new KeystoreFileServiceImpl(repository, userRepository, "./temp-output/", systemPropertyService, fileService);
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
	void shouldDeleteSomeTempFiles() {
		Path p1 = initMockPath("t/", true);
		Path p2 = initMockPath("file1.txt", false);
		Path p3 = initMockPath("file2.txt", false);
		Path p4 = initMockPath("file3.txt", false);

        when(fileService.list(any(Path.class))).thenReturn(Stream.of(p1, p2, p3, p4));
		when(fileService.delete(p1)).thenReturn(true);
		when(fileService.delete(p2)).thenReturn(false);
		when(fileService.delete(p3)).thenReturn(true);
		when(fileService.delete(p4)).thenThrow(new IOException("Oops!"));
		when(repository.findByFileName("file1.txt")).thenReturn("file1.txt");
		when(repository.findByFileName("file2.txt")).thenReturn(null);
		when(repository.findByFileName("file3.txt")).thenReturn(null);

		// act
        long response = service.deleteTempKeystoreFiles();

		// assert
        assertEquals(1L, response);

		verify(fileService).list(any(Path.class));
		verify(fileService, times(2)).delete(any(Path.class));
		verify(repository).findByFileName("file1.txt");
		verify(repository).findByFileName("file2.txt");
		verify(repository).findByFileName("file3.txt");
	}

	@Test
	@SneakyThrows
	void shouldDeleteTempFilesFail() {
		Path path1 = mock(Path.class);
		File mockFile1 = mock(File.class);
		when(mockFile1.isDirectory()).thenReturn(false);
		when(mockFile1.getName()).thenReturn("file1.txt");
		when(path1.toFile()).thenReturn(mockFile1);

        when(fileService.list(any(Path.class))).thenThrow(new IOException("ERROR!"));

        when(repository.findByFileName("file1.txt")).thenReturn(null);

        // act
        GmsException response = assertThrows(GmsException.class, () -> service.deleteTempKeystoreFiles());

        // assert
        assertEquals("ERROR!", Throwables.getRootCause(response).getMessage());
		verify(fileService).list(any(Path.class));
	}

	private Path initMockPath(String fileName, boolean isDirectory) {
		Path path1 = mock(Path.class);
		File mockFile1 = mock(File.class);
		when(mockFile1.isDirectory()).thenReturn(isDirectory);

		if (!isDirectory) {
			when(mockFile1.getName()).thenReturn(fileName);
		}

		when(path1.toFile()).thenReturn(mockFile1);
		return path1;
	}
}