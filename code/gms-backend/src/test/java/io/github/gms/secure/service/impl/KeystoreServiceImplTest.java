package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

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
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.entity.KeystoreEntity;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeyStoreValueType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.event.EntityDisabledEvent;
import io.github.gms.common.event.EntityDisabledEvent.EntityType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.converter.KeystoreConverter;
import io.github.gms.secure.dto.GetSecureValueDto;
import io.github.gms.secure.dto.IdNamePairDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.CryptoService;
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

	private static final String JKS_TEST_FILE_LOCATION = "./test-output/2/test.jks";

	@InjectMocks
	private KeystoreServiceImpl service = new KeystoreServiceImpl();

	@Mock
	private CryptoService cryptoService;

	@Mock
	private KeystoreRepository repository;

	@Mock
	private KeystoreConverter converter;

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private Gson gson = TestUtils.getGson();
	
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@AfterAll
	@SneakyThrows
	public static void tearDown() {
		TestUtils.deleteDirectoryWithContent("./test-output");
	}

	@Override
	@BeforeEach
	public void setup() {
		super.setup();

		((Logger) LoggerFactory.getLogger(KeystoreServiceImpl.class)).addAppender(logAppender);
		ReflectionTestUtils.setField(service, "keystorePath", "test-output/");

		MDC.put(MdcParameter.USER_ID.getDisplayName(), 2L);
	}

	@Test
	void shouldNotSupportSave() {
		UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
				() -> service.save(new SaveKeystoreRequestDto()));

		// assert
		assertEquals("Not supported!", exception.getMessage());
	}

	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldSaveNewEntityFailCausedByKeystoreValidation() {
		// arrange
		SaveKeystoreRequestDto dtoInput = TestUtils.createSaveKeystoreRequestDto();
		dtoInput.setId(1L);
		String model = TestUtils.getGson().toJson(dtoInput);

		MultipartFile multiPart = mock(MultipartFile.class);
		when(multiPart.getBytes()).thenReturn("test".getBytes());
		when(gson.fromJson(eq(model), any(Class.class))).thenReturn(dtoInput);
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		doThrow(new RuntimeException("Test failure")).when(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));

		// assert
		assertEquals("java.lang.RuntimeException: Test failure", exception.getMessage());
		verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository, never()).save(any());
		verify(gson).fromJson(eq(model), any(Class.class));
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
		TestUtils.assertLogContains(logAppender, "Keystore validation failed");
	}
	
	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldNotSaveNewEntityBecauseOfInvalidJson() {
		// arrange
		String model = "{invalidJson}";
		MultipartFile multiPart = mock(MultipartFile.class);

		when(gson.fromJson(eq(model), any(Class.class))).thenThrow(new RuntimeException("Error!"));

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));

		// assert
		assertEquals("java.lang.RuntimeException: Error!", exception.getMessage());
		verify(converter, never()).toNewEntity(any(), eq(multiPart));
		verify(cryptoService, never()).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(gson).fromJson(eq(model), any(Class.class));
	}
	
	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldNotSaveNewEntityBecauseOfMissingFile() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		String model = TestUtils.getGson().toJson(dto);
		when(gson.fromJson(eq(model), any(Class.class))).thenReturn(dto);

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, null));

		// assert
		assertEquals("Keystore file must be provided!", exception.getMessage());
		verify(cryptoService, never()).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository, never()).save(any());
		verify(gson).fromJson(eq(model), any(Class.class));
	}
	
	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldNotSaveNewEntity() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		String model = TestUtils.getGson().toJson(dto);
		
		MultipartFile multiPart = mock(MultipartFile.class);
		when(gson.fromJson(eq(model), any(Class.class))).thenReturn(dto);
		when(repository.getAllKeystoreNames(anyLong(), anyString())).thenReturn(Lists.newArrayList("keystore"));

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));

		// assert
		assertEquals("Keystore name must be unique!", exception.getMessage());
		verify(converter, never()).toNewEntity(any(), eq(multiPart));
		verify(cryptoService, never()).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository, never()).save(any());
		verify(gson).fromJson(eq(model), any(Class.class));
	}

	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldSaveNewEntityFailedByCopyError() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 6L);
		try(MockedStatic<Files> staticFiles = Mockito.mockStatic(Files.class)) {
			staticFiles.when(() -> Files.createDirectories(Path.of("test-output/6/my-key.jks"))).thenThrow(new RuntimeException("Invalid"));
			SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
			dto.setUserId(6L);
			String model = TestUtils.getGson().toJson(dto);
			
			MultipartFile multiPart = mock(MultipartFile.class);
			when(multiPart.getOriginalFilename()).thenReturn("my-key2.jks");
			when(multiPart.getBytes()).thenReturn("test".getBytes());
	
			when(converter.toNewEntity(any(), eq(multiPart))).thenReturn(TestUtils.createKeystoreEntity());
			when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
			when(gson.fromJson(eq(model), any(Class.class))).thenReturn(dto);
	
			// act
			GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));
	
			// assert
			assertEquals("java.io.FileNotFoundException: test-output\\6\\my-key2.jks (A rendszer nem találja a megadott elérési utat)", exception.getMessage());
			verify(converter).toNewEntity(any(), eq(multiPart));
			verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
			verify(repository).save(any());
			verify(gson).fromJson(eq(model), any(Class.class));
		}
	}
	
	@Test
	@SneakyThrows
	@SuppressWarnings("unchecked")
	void shouldSaveNewEntity() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		String model = TestUtils.getGson().toJson(dto);
		
		MultipartFile multiPart = mock(MultipartFile.class);

		when(multiPart.getOriginalFilename()).thenReturn("my-key.jks");
		when(multiPart.getBytes()).thenReturn("test".getBytes());
		when(converter.toNewEntity(any(), eq(multiPart))).thenReturn(TestUtils.createKeystoreEntity());
		when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
		when(gson.fromJson(eq(model), any(Class.class))).thenReturn(dto);

		// act
		service.save(model, multiPart);

		// assert
		verify(converter).toNewEntity(any(), eq(multiPart));
		verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository).save(any());
		verify(gson).fromJson(eq(model), any(Class.class));
	}
	
	@Test
	@SneakyThrows
	void shouldSaveEntityWithoutFile() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.setId(1L);
		String model = TestUtils.getGson().toJson(dto);

		when(converter.toEntity(any(), any(), isNull())).thenReturn(TestUtils.createKeystoreEntity());
		when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
		when(gson.fromJson(eq(model), any())).thenReturn(dto);
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		
		new File("test-output/2/").mkdirs();

		FileWriter fileWriter = new FileWriter(JKS_TEST_FILE_LOCATION);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print("value");
		printWriter.close();

		// act
		service.save(model, null);

		// assert
		verify(converter).toEntity(any(), any(), isNull());
		verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository).save(any());
		verify(gson).fromJson(eq(model), any());
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
		
		Files.deleteIfExists(Paths.get(JKS_TEST_FILE_LOCATION));
	}

	@Test
	@SneakyThrows
	void shouldSaveEntity() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.setStatus(EntityStatus.DISABLED);
		dto.setId(1L);
		String model = TestUtils.getGson().toJson(dto);
		
		MultipartFile multiPart = mock(MultipartFile.class);
		when(multiPart.getOriginalFilename()).thenReturn("my-key.jks");
		when(multiPart.getBytes()).thenReturn("test".getBytes());

		when(converter.toEntity(any(), any(), eq(multiPart))).thenReturn(TestUtils.createKeystoreEntity());
		when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
		when(gson.fromJson(eq(model), any())).thenReturn(dto);
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
		
		KeystoreEntity savedEntity = TestUtils.createKeystoreEntity();
		savedEntity.setStatus(EntityStatus.DISABLED);
		when(repository.save(any(KeystoreEntity.class))).thenReturn(savedEntity);

		// act
		service.save(model, multiPart);

		// assert
		verify(converter).toEntity(any(), any(), eq(multiPart));
		verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
		verify(repository).save(any());
		verify(gson).fromJson(eq(model), any());
		
		ArgumentCaptor<EntityDisabledEvent> entityDisabledEventCaptor = ArgumentCaptor.forClass(EntityDisabledEvent.class);
		verify(applicationEventPublisher).publishEvent(entityDisabledEventCaptor.capture());
		
		EntityDisabledEvent capturedEvent = entityDisabledEventCaptor.getValue();
		assertEquals(2L, capturedEvent.getUserId());
		assertEquals(1L, capturedEvent.getId());
		assertEquals(EntityType.KEYSTORE, capturedEvent.getType());
	}
	
	@Test
	@SneakyThrows
	void shouldNotSaveEntity() {
		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		dto.setId(1L);
		String model = TestUtils.getGson().toJson(dto);
		
		MultipartFile multiPart = mock(MultipartFile.class);

		when(gson.fromJson(eq(model), any())).thenReturn(dto);
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
		when(converter.toDto(any())).thenReturn(new KeystoreDto());

		// act
		KeystoreDto response = service.getById(1L);

		// assert
		assertNotNull(response);
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
		verify(converter).toDto(any());
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
		when(repository.findByIdAndUserId(anyLong(), anyLong()))
				.thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

		// act
		service.delete(1L);

		// assert
		TestUtils.assertLogContains(logAppender, "Keystore file cannot be deleted");
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
	}

	@Test
	@SneakyThrows
	void shouldDelete() {
		new File("test-output/1/").mkdirs();

		FileWriter fileWriter = new FileWriter("test-output/1/test.jks");
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

		// act
		String response = service.getValue(new GetSecureValueDto(1L, input.getValueType()));

		// assert
		assertEquals(input.getExpectedValue(), response);
		verify(repository).findByIdAndUserId(anyLong(), anyLong());
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
	
	public static List<ValueHolder> valueData() {
		return Lists.newArrayList(
				new ValueHolder(KeyStoreValueType.KEYSTORE_ALIAS, "test"),
				new ValueHolder(KeyStoreValueType.KEYSTORE_ALIAS_CREDENTIAL, "test"),
				new ValueHolder(KeyStoreValueType.KEYSTORE_CREDENTIAL, "test")
		);
	}
}
