package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.util.Lists;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.AliasOperation;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeyStoreValueType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.event.EntityChangeEvent;
import io.github.gms.common.event.EntityChangeEvent.EntityChangeType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.converter.KeystoreConverter;
import io.github.gms.secure.dto.DownloadFileResponseDto;
import io.github.gms.secure.dto.GetSecureValueDto;
import io.github.gms.secure.dto.IdNamePairDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.KeystoreAliasDto;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.entity.KeystoreEntity;
import io.github.gms.secure.repository.KeystoreAliasRepository;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.CryptoService;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import io.github.gms.util.TestUtils.ValueHolder;
import lombok.SneakyThrows;

/**
 * Unit test of {@link KeystoreServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class KeystoreServiceImplTest extends AbstractLoggingUnitTest {

	private static final String JKS_TEST_FILE_LOCATION = "./unit-test-output/" + DemoData.USER_1_ID + "/test.jks";

	@InjectMocks
	private KeystoreServiceImpl service;

	@Mock
	private CryptoService cryptoService;

	@Mock
	private KeystoreRepository repository;
	
	@Mock
	private KeystoreAliasRepository aliasRepository;

	@Mock
	private KeystoreConverter converter;

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private ObjectMapper objectMapper = TestUtils.objectMapper();
	
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@AfterAll
	@SneakyThrows
	public static void tearDownAll() {
		TestUtils.deleteDirectoryWithContent("./unit-test-output");
	}

	@Override
	@BeforeEach
	public void setup() {
		super.setup();

		((Logger) LoggerFactory.getLogger(KeystoreServiceImpl.class)).addAppender(logAppender);
		ReflectionTestUtils.setField(service, "keystorePath", "unit-test-output/");
		ReflectionTestUtils.setField(service, "keystoreTempPath", "temp-output/");

		MDC.put(MdcParameter.USER_ID.getDisplayName(), DemoData.USER_1_ID);
	}

	@Test
	void shouldNotSupportSave() {
		// act & assert
		TestUtils.assertException(UnsupportedOperationException.class,() -> service.save(new SaveKeystoreRequestDto()), "Not supported!");
	}

	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldNotSaveNewEntityCausedByInvalidKeystoreFile() {
		try (MockedStatic<Files> mockedStaticFiles = mockStatic(Files.class)) {
			mockedStaticFiles.when(() -> Files.readAllBytes(any(Path.class))).thenThrow(new RuntimeException("Test failure"));
			mockedStaticFiles.when(() -> Files.exists(any(Path.class))).thenAnswer(new Answer<Boolean>() {
	
				@Override
				public Boolean answer(InvocationOnMock invocation) throws Throwable {
					Path path = invocation.getArgument(0);
					return path.toString().equals("temp-output\\generated-fail.jks");
				}
			});
	
			// arrange
			SaveKeystoreRequestDto dtoInput = TestUtils.createSaveKeystoreRequestDto();
			dtoInput.setId(null);
			dtoInput.setGeneratedFileName("generated-fail.jks");
			String model = TestUtils.objectMapper().writeValueAsString(dtoInput);
	
			when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dtoInput);
			when(converter.toNewEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());
	
			// act
			GmsException exception = assertThrows(GmsException.class, () -> service.save(model, null));
	
			// assert
			assertEquals("java.lang.RuntimeException: Test failure", exception.getMessage());
			verify(repository, never()).save(any());
			verify(converter).toNewEntity(any(), any());
			verify(objectMapper).readValue(eq(model), any(Class.class));
			TestUtils.assertLogContains(logAppender, "Keystore content cannot be parsed");
		}
	}
	
	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldNotSaveNewEntityWhenKeystoreFileIsMissing() {
		MockedStatic<Files> mockedStaticFiles = mockStatic(Files.class);
		mockedStaticFiles.when(() -> Files.readAllBytes(any(Path.class))).thenThrow(new RuntimeException("Test failure"));
		mockedStaticFiles.when(() -> Files.exists(any(Path.class))).thenReturn(false);

		// arrange
		SaveKeystoreRequestDto dtoInput = TestUtils.createSaveKeystoreRequestDto();
		dtoInput.setId(null);
		dtoInput.setGeneratedFileName("generated-fail.jks");
		String model = TestUtils.objectMapper().writeValueAsString(dtoInput);

		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dtoInput);
		when(converter.toNewEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, null));
		
		mockedStaticFiles.close();

		// assert
		assertEquals("Keystore file does not exist!", exception.getMessage());
		verify(repository, never()).save(any());
		verify(converter).toNewEntity(any(), any());
		verify(objectMapper).readValue(eq(model), any(Class.class));

		
	}
	
	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldNotSaveNewEntityBecauseOfInvalidJson() {
		// arrange
		String model = "{invalidJson}";
		MultipartFile multiPart = mock(MultipartFile.class);

		when(objectMapper.readValue(eq(model), any(Class.class))).thenThrow(new RuntimeException("Error!"));

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));

		// assert
		assertEquals("java.lang.RuntimeException: Error!", exception.getMessage());
		verify(converter, never()).toNewEntity(any(), eq(multiPart));
		verify(cryptoService, never()).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(objectMapper).readValue(eq(model), any(Class.class));
	}
	
	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldNotSaveNewEntityBecauseOfMissingFile() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.setId(null);
		String model = TestUtils.objectMapper().writeValueAsString(dto);
		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, null));

		// assert
		assertEquals("Keystore file must be provided!", exception.getMessage());
		verify(cryptoService, never()).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository, never()).save(any());
		verify(objectMapper).readValue(eq(model), any(Class.class));
	}
	
	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldNotSaveNewEntityWhenKeystoreNameMustBeUnique() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.setId(null);
		String model = TestUtils.objectMapper().writeValueAsString(dto);
		
		MultipartFile multiPart = mock(MultipartFile.class);
		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);
		when(repository.countAllKeystoresByName(anyLong(), anyString())).thenReturn(1l);

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));

		// assert
		assertEquals("Keystore name must be unique!", exception.getMessage());
		verify(converter, never()).toNewEntity(any(), eq(multiPart));
		verify(cryptoService, never()).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository).countAllKeystoresByName(anyLong(), anyString());
		verify(repository, never()).save(any());
		verify(objectMapper).readValue(eq(model), any(Class.class));
	}

	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldSaveNewEntityFailedByCopyError() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 6L);
		try(MockedStatic<Files> staticFiles = Mockito.mockStatic(Files.class)) {
			staticFiles.when(() -> Files.createDirectories(any(Path.class))).thenThrow(new FileNotFoundException("Invalid"));
			SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
			dto.setId(null);
			dto.setUserId(6L);
			String model = TestUtils.objectMapper().writeValueAsString(dto);
			
			MultipartFile multiPart = mock(MultipartFile.class);
			//when(multiPart.getOriginalFilename()).thenReturn("test.jks");
			when(multiPart.getBytes()).thenReturn("test".getBytes());
	
			when(converter.toNewEntity(any(), eq(multiPart))).thenReturn(TestUtils.createKeystoreEntity());
			when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
			when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);

			// act
			GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));
	
			// assert
			assertTrue(exception.getMessage().startsWith("java.io.FileNotFoundException"));
			verify(converter).toNewEntity(any(), eq(multiPart));
			verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
			verify(repository).save(any());
			verify(objectMapper).readValue(eq(model), any(Class.class));
		}
	}
	
	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldNotSaveNewEntityWithNonUniqueFileName() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.setId(null);
		String model = TestUtils.objectMapper().writeValueAsString(dto);

		MultipartFile multiPart = mock(MultipartFile.class);
		when(multiPart.getBytes()).thenReturn("test".getBytes());

		KeystoreEntity keystoreEntity = TestUtils.createKeystoreEntity();
		keystoreEntity.setFileName("my-key.jks");
		when(repository.save(any())).thenReturn(keystoreEntity);

		when(converter.toNewEntity(any(), eq(multiPart))).thenReturn(TestUtils.createKeystoreEntity());
		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));

		// assert
		assertEquals("File name must be unique!", exception.getMessage());
		verify(converter).toNewEntity(any(), eq(multiPart));
		verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository).save(any());
		verify(objectMapper).readValue(eq(model), any(Class.class));
	}
	
	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldSaveNewEntity() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.setId(null);
		String model = TestUtils.objectMapper().writeValueAsString(dto);
		
		MultipartFile multiPart = mock(MultipartFile.class);

		when(multiPart.getBytes()).thenReturn("test".getBytes());
		when(converter.toNewEntity(any(), eq(multiPart))).thenReturn(TestUtils.createKeystoreEntity());

		KeystoreEntity keystoreEntity = TestUtils.createKeystoreEntity();
		keystoreEntity.setFileName("my-key.jks");
		when(repository.save(any())).thenReturn(keystoreEntity);
		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);

		// act
		service.save(model, multiPart);

		// assert
		verify(converter).toNewEntity(any(), eq(multiPart));
		verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository).save(any());
		verify(objectMapper).readValue(eq(model), any(Class.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@SneakyThrows
	void shouldSaveEntityWithoutFile() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		String model = TestUtils.objectMapper().writeValueAsString(dto);

		when(converter.toEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());
		when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		
		new File("unit-test-output/" + DemoData.USER_1_ID + "/").mkdirs();

		FileWriter fileWriter = new FileWriter(JKS_TEST_FILE_LOCATION);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print("value");
		printWriter.close();

		// act
		service.save(model, null);

		// assert
		verify(converter).toEntity(any(), any());
		verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository).save(any());
		verify(objectMapper).readValue(eq(model), any(Class.class));
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
		
		Files.deleteIfExists(Paths.get(JKS_TEST_FILE_LOCATION));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@SneakyThrows
	void shouldNotSaveEntityCausedByMissingAlias() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.setAliases(List.of());
		dto.setId(1L);
		String model = TestUtils.objectMapper().writeValueAsString(dto);
		
		MultipartFile multiPart = mock(MultipartFile.class);
		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));

		// assert
		assertEquals("You must define at least one keystore alias!", exception.getMessage());
		verify(objectMapper).readValue(eq(model), any(Class.class));
	}

	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldNotSaveEntityOccurredWhenOnlyDeletedAliasProvided() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.setAliases(List.of(new KeystoreAliasDto(1L, "alias", "test", AliasOperation.DELETE)));
		dto.setId(1L);
		String model = TestUtils.objectMapper().writeValueAsString(dto);
		
		MultipartFile multiPart = mock(MultipartFile.class);
		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));

		// assert
		assertEquals("You must define at least one keystore alias!", exception.getMessage());
		verify(objectMapper).readValue(eq(model), any(Class.class));
	}
	
	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldNotSaveEntityWhenBothFileInputProvided() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.setId(1L);
		dto.setGeneratedFileName(UUID.randomUUID().toString() + ".jks");
		String model = TestUtils.objectMapper().writeValueAsString(dto);
		
		MultipartFile multiPart = mock(MultipartFile.class);
		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));

		// assert
		assertEquals("Only one keystore source is allowed!", exception.getMessage());
		verify(objectMapper).readValue(eq(model), any(Class.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	@SneakyThrows
	void shouldSaveEntity() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.getAliases().add(new KeystoreAliasDto(3L, "alias2", "test", AliasOperation.DELETE));
		dto.getAliases().add(new KeystoreAliasDto(4L, "alias3", "test", AliasOperation.SAVE));
		dto.setStatus(EntityStatus.DISABLED);
		dto.setId(1L);
		String model = TestUtils.objectMapper().writeValueAsString(dto);
		
		MultipartFile multiPart = mock(MultipartFile.class);
		when(multiPart.getBytes()).thenReturn("test".getBytes());

		when(converter.toEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());
		when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		
		KeystoreEntity savedEntity = TestUtils.createKeystoreEntity();
		savedEntity.setStatus(EntityStatus.DISABLED);
		when(repository.save(any(KeystoreEntity.class))).thenReturn(savedEntity);

		// act
		service.save(model, multiPart);

		// assert
		verify(converter).toEntity(any(), any());
		verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository).save(any());
		verify(objectMapper).readValue(eq(model), any(Class.class));
		
		ArgumentCaptor<EntityChangeEvent> entityDisabledEventCaptor = ArgumentCaptor.forClass(EntityChangeEvent.class);
		verify(applicationEventPublisher, times(2)).publishEvent(entityDisabledEventCaptor.capture());
		
		EntityChangeEvent capturedEvent = entityDisabledEventCaptor.getValue();
		assertEquals(1L, Long.class.cast(capturedEvent.getMetadata().get("userId")));
		assertEquals(1L, Long.class.cast(capturedEvent.getMetadata().get("keystoreId")));
		assertEquals(EntityChangeType.KEYSTORE_DISABLED, capturedEvent.getType());
	}

	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldSaveNewEntityWhenGeneratedInputIsAvailable() {
		//new File("unit-test-output/1/").mkdirs();
		//File generatedJks = new File("unit-test-output/1/generated.jks");
		Path newFilePath = Files.createFile(Paths.get("unit-test-output/1/generated.jks"));
		Files.writeString(newFilePath, "test");

		/*try (MockedStatic<Files> mockedStaticFiles = mockStatic(Files.class)) {
			//mockedStaticFiles.when(() -> Files.readAllBytes(any(Path.class))).thenReturn("test".getBytes());
			//mockedStaticFiles.when(() -> Files.exists(any(Path.class))).thenAnswer(new Answer<Boolean>() {
	
				@Override
				public Boolean answer(InvocationOnMock invocation) throws Throwable {
					Path path = invocation.getArgument(0);
					
					return path.toString().equals("temp-output\\generated.jks");
				}
			});*/
	
			// arrange
			SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
			dto.setGeneratedFileName("generated.jks");
			dto.setId(null);
			String model = TestUtils.objectMapper().writeValueAsString(dto);
	
			when(converter.toNewEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());
			when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
			when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);
			
			KeystoreEntity savedEntity = TestUtils.createKeystoreEntity();
			when(repository.save(any(KeystoreEntity.class))).thenReturn(savedEntity);
	
			// act
			service.save(model, null);
	
			// assert
			verify(converter).toNewEntity(any(), any());
			verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
			verify(repository).save(any());
			verify(objectMapper).readValue(eq(model), any(Class.class));
		//}

		newPath.delete();
		Paths.get("unit-test-output/1/").toFile().delete();
	}

	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldSaveEntityWithoutFileInputs() {
		// arrange
		MockedStatic<Files> mockedStaticFiles = mockStatic(Files.class);
		mockedStaticFiles.when(() -> Files.readAllBytes(any(Path.class))).thenReturn("test".getBytes());
		mockedStaticFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);

		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.getAliases().add(new KeystoreAliasDto(3L, "alias2", "test", AliasOperation.DELETE));
		dto.setStatus(EntityStatus.DISABLED);
		dto.setId(1L);
		dto.setGeneratedFileName(null);
		String model = TestUtils.objectMapper().writeValueAsString(dto);

		when(converter.toEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());
		when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		
		KeystoreEntity savedEntity = TestUtils.createKeystoreEntity();
		savedEntity.setStatus(EntityStatus.DISABLED);
		when(repository.save(any(KeystoreEntity.class))).thenReturn(savedEntity);

		// act
		service.save(model, null);

		// assert
		verify(converter).toEntity(any(), any());
		verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository).save(any());
		verify(objectMapper).readValue(eq(model), any(Class.class));
		
		ArgumentCaptor<EntityChangeEvent> entityDisabledEventCaptor = ArgumentCaptor.forClass(EntityChangeEvent.class);
		verify(applicationEventPublisher, times(2)).publishEvent(entityDisabledEventCaptor.capture());
		
		EntityChangeEvent capturedEvent = entityDisabledEventCaptor.getValue();
		assertEquals(1L, Long.class.cast(capturedEvent.getMetadata().get("userId")));
		assertEquals(1L, Long.class.cast(capturedEvent.getMetadata().get("keystoreId")));
		assertEquals(EntityChangeType.KEYSTORE_DISABLED, capturedEvent.getType());
		
		mockedStaticFiles.close();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@SneakyThrows
	void shouldNotSaveEntity() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.setId(1L);
		String model = TestUtils.objectMapper().writeValueAsString(dto);
		
		MultipartFile multiPart = mock(MultipartFile.class);

		when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));
		assertEquals(Constants.ENTITY_NOT_FOUND, exception.getMessage());
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
	}

	@Test
	void shouldNotFindById() {
		// arrange
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.getById(1L));

		// assert
		assertEquals(Constants.ENTITY_NOT_FOUND, exception.getMessage());
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
	}

	@Test
	void shouldFindById() {
		// arrange
		when(repository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(converter.toDto(any(), anyList())).thenReturn(new KeystoreDto());

		// act
		KeystoreDto response = service.getById(1L);

		// assert
		assertNotNull(response);
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
		verify(converter).toDto(any(), anyList());
	}

	@Test
	void shouldReturnEmptyList() {
		// arrange
		when(repository.findAllByUserId(anyLong(), any(Pageable.class))).thenThrow(new RuntimeException("Unexpected error!"));

		// act
		KeystoreListDto response = service.list(new PagingDto("ASC", "id", 0, 10));

		// assert
		assertNotNull(response);
		assertEquals(0, response.getResultList().size());
		verify(repository).findAllByUserId(anyLong(), any(Pageable.class));
		verify(converter, never()).toDtoList(any());
	}
	
	@Test
	void shouldReturnList() {
		// arrange
		Page<KeystoreEntity> mockList = new PageImpl<>(Lists.newArrayList(TestUtils.createKeystoreEntity()));
		when(repository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(mockList);
		when(converter.toDtoList(any())).thenReturn(new KeystoreListDto(Lists.newArrayList(new KeystoreDto())));

		// act
		KeystoreListDto response = service.list(new PagingDto("ASC", "id", 0, 10));

		// assert
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		verify(repository).findAllByUserId(anyLong(), any(Pageable.class));
		verify(converter).toDtoList(any());
	}

	@Test
	void shouldNotDeleteBecauseFileIsMissing() {
		// arrange
		MockedStatic<Files> mockedStatic = mockStatic(Files.class);
		mockedStatic.when(() -> Files.delete(any(Path.class))).thenThrow(FileNotFoundException.class);
		when(repository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

		// act
		service.delete(11L);

		// assert
		TestUtils.assertLogContains(logAppender, "Keystore file cannot be deleted");
		verify(repository).findByIdAndUserId(anyLong(), anyLong());

		mockedStatic.close();
	}

	@Test
	@SneakyThrows
	void shouldDelete() {
		new File("unit-test-output/1/").mkdirs();

		FileWriter fileWriter = new FileWriter("unit-test-output/1/test.jks");
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print("value");
		printWriter.close();

		// arrange
		when(repository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

		// act
		service.delete(1L);

		// assert
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
		verify(repository).deleteById(1L);
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void shouldToggleStatus(boolean enabled) {
		// arrange
		when(repository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

		// act
		service.toggleStatus(1L, enabled);

		// assert
		verify(repository).save(any());
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
	}

	@ParameterizedTest
	@MethodSource("valueData")
	void shouldGetValue(ValueHolder input) {
		// arrange
		when(repository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		
		if (input.getAliasId() != null) {
			when(aliasRepository.findByIdAndKeystoreId(anyLong(), anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
		}

		// act
		String response = service.getValue(new GetSecureValueDto(1L, 1L, input.getValueType()));

		// assert
		assertEquals(input.getExpectedValue(), response);
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
		
		if (input.getAliasId() != null) {
			verify(aliasRepository).findByIdAndKeystoreId(anyLong(), anyLong());
		}
	}
	
	@Test
	void shouldNotGetValue() {
		// arrange
		when(repository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		when(aliasRepository.findByIdAndKeystoreId(anyLong(), anyLong())).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> getValue());

		// assert
		assertEquals(Constants.ENTITY_NOT_FOUND, exception.getMessage());
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
		verify(aliasRepository).findByIdAndKeystoreId(anyLong(), anyLong());
	}
	
	private void getValue() {
		service.getValue(new GetSecureValueDto(1L, 1L, KeyStoreValueType.KEYSTORE_ALIAS));
	}
	
	@Test
	void shouldCount() {
		// arrange
		when(repository.countByUserId(anyLong())).thenReturn(3L);

		// act
		LongValueDto response = service.count();
		
		// assert
		assertNotNull(response);
		assertEquals(3L, response.getValue());
	}
	
	@Test
	void shouldGetAllKeystoreNames() {
		// arrange
		when(repository.getAllKeystoreNames(anyLong())).thenReturn(Lists.newArrayList(
				new IdNamePairDto(1L, "keystore1"),
				new IdNamePairDto(1L, "keystore2")
		));

		// act
		IdNamePairListDto response = service.getAllKeystoreNames();
		
		// assert
		assertNotNull(response);
		assertEquals(2, response.getResultList().size());
		assertEquals("keystore1", response.getResultList().get(0).getName());
		verify(repository).getAllKeystoreNames(anyLong());
	}
	
	@Test
	void shouldGetAllKeystoreAliasNames() {
		// arrange
		when(aliasRepository.getAllAliasNames(anyLong())).thenReturn(Lists.newArrayList(
				new IdNamePairDto(1L, "alias1"),
				new IdNamePairDto(1L, "alias2")
		));
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

		// act
		IdNamePairListDto response = service.getAllKeystoreAliasNames(1L);
		
		// assert
		assertNotNull(response);
		assertEquals(2, response.getResultList().size());
		assertEquals("alias1", response.getResultList().get(0).getName());
		verify(aliasRepository).getAllAliasNames(anyLong());
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
	}
	
	@Test
	void shouldNotDownloadFile() {
		// arrange
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
			
			mockedFiles.when(() -> Files.readAllBytes(any(Path.class))).thenThrow(new RuntimeException("File cannot be downloaded"));
	
			// act
			GmsException exception = assertThrows(GmsException.class, () -> service.downloadKeystore(1L));
	
			// assert
			assertEquals("java.lang.RuntimeException: File cannot be downloaded", exception.getMessage());
			
			mockedFiles.verify(() -> Files.readAllBytes(any(Path.class)));
			
		}
	}
	
	@Test
	void shouldDownloadFile() {
		// arrange
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		MockedStatic<Files> mockedFiles = mockStatic(Files.class);
		
		mockedFiles.when(() -> Files.readAllBytes(any(Path.class))).thenReturn("test".getBytes());

		// act
		DownloadFileResponseDto response = service.downloadKeystore(1L);
		
		// assert
		assertNotNull(response);
		assertEquals("test.jks", response.getFileName());
		assertEquals("test", new String(response.getFileContent()));
		
		mockedFiles.close();
	}
	
	@Test
	void shouldGenerateKeystore() {
		assertEquals("generated.jks", service.generateKeystore(new SaveKeystoreRequestDto()));
	}
	
	public static List<ValueHolder> valueData() {
		return Lists.newArrayList(
				new ValueHolder(KeyStoreValueType.KEYSTORE_ALIAS, 1L, "test"),
				new ValueHolder(KeyStoreValueType.KEYSTORE_ALIAS_CREDENTIAL, 1L, "test"),
				new ValueHolder(KeyStoreValueType.KEYSTORE_CREDENTIAL, null, "test")
		);
	}
}
