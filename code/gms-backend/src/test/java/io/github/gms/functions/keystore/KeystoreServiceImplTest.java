package io.github.gms.functions.keystore;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.IdNamePairDto;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.enums.AliasOperation;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeyStoreValueType;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.model.EnabledAlgorithm;
import io.github.gms.common.model.EntityChangeEvent;
import io.github.gms.common.model.EntityChangeEvent.EntityChangeType;
import io.github.gms.common.service.CryptoService;
import io.github.gms.common.service.FileService;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.secret.GetSecureValueDto;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import io.github.gms.util.TestUtils.ValueHolder;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.gms.common.util.Constants.ENTITY_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Nested
class KeystoreServiceImplTest extends AbstractLoggingUnitTest {

    private static final String JKS_TEST_FILE_LOCATION = "./unit-test-output/" + DemoData.USER_1_ID + "/my-key.jks";
    //private static final AtomicInteger counter = new AtomicInteger(0);

    private KeystoreServiceImpl service;
    private CryptoService cryptoService;
    private KeystoreRepository repository;
    private KeystoreAliasRepository aliasRepository;
    private KeystoreConverter converter;
    private ObjectMapper objectMapper;
    private ApplicationEventPublisher applicationEventPublisher;
    private KeystoreFileService keystoreFileService;
    private FileService fileService;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();

        // Init
        cryptoService = mock(CryptoService.class);
        repository = mock(KeystoreRepository.class);
        aliasRepository = mock(KeystoreAliasRepository.class);
        converter = mock(KeystoreConverter.class);
        objectMapper = mock(ObjectMapper.class);
        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        keystoreFileService = mock(KeystoreFileService.class);
        fileService = mock(FileService.class);
        service = new KeystoreServiceImpl(cryptoService, repository, aliasRepository, converter, objectMapper,
                applicationEventPublisher, keystoreFileService);

        ((Logger) LoggerFactory.getLogger(KeystoreServiceImpl.class)).addAppender(logAppender);

        service.setFileService(fileService);
        service.setKeystorePath("unit-test-output/");
        service.setKeystoreTempPath("temp-output/");

        TestUtils.createDirectory("unit-test-output/");
        TestUtils.createDirectory("temp-output/");

        MDC.put(MdcParameter.USER_ID.getDisplayName(), DemoData.USER_1_ID);
    }

    @AfterEach
    @SneakyThrows
    public void tearDown() {
        TestUtils.deleteDirectoryWithContent("./unit-test-output/");
        TestUtils.deleteDirectoryWithContent("./temp-output/");
    }

    @Test
    void shouldNotSupportSave() {
        // act & assert
        TestUtils.assertException(UnsupportedOperationException.class, () -> service.save(new SaveKeystoreRequestDto()),
                "Not supported!");
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void shouldNotSaveNewEntityCausedByInvalidKeystoreFile() {
        when(fileService.readAllBytes(any(Path.class)))
                .thenThrow(new RuntimeException("Test failure"));

        // arrange
        SaveKeystoreRequestDto dtoInput = TestUtils.createSaveKeystoreRequestDto();
        dtoInput.setId(null);
        dtoInput.setGenerated(true);
        String model = TestUtils.objectMapper().writeValueAsString(dtoInput);

        when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dtoInput);
        when(converter.toNewEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());
        when(keystoreFileService.generate(any(SaveKeystoreRequestDto.class))).thenReturn("filename.jks");
        when(fileService.exists(any(Path.class))).thenReturn(true);

        // act
        GmsException exception = assertThrows(GmsException.class, () -> service.save(model, null));

        // assert
        assertEquals("java.lang.RuntimeException: Test failure", exception.getMessage());
        verify(repository, never()).save(any());
        verify(converter).toNewEntity(any(), any());
        verify(objectMapper).readValue(eq(model), any(Class.class));
        TestUtils.assertLogContains(logAppender, "Keystore content cannot be parsed");
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {
            "hack/../../root/etc/password",
            "/root/etc/password"
    })
    public void shouldNotSaveNewEntityCausedByVulnerableKeystoreFile(String mockValue) {
        // arrange
        MultipartFile multiPart = mock(MultipartFile.class);
        when(multiPart.getOriginalFilename()).thenReturn(mockValue);

        SaveKeystoreRequestDto dtoInput = TestUtils.createSaveKeystoreRequestDto();
        dtoInput.setId(null);
        dtoInput.setGenerated(false);
        String model = TestUtils.objectMapper().writeValueAsString(dtoInput);
        when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dtoInput);
        when(converter.toNewEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());

        // act
        GmsException exception = assertThrows(GmsException.class, () -> service.save(model, multiPart));

        // assert
        assertEquals("Could not upload file!", exception.getMessage());
        verify(repository, never()).save(any());
        verify(converter).toNewEntity(any(), any());
        verify(objectMapper).readValue(eq(model), any(Class.class));
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void shouldNotSaveNewEntityWhenKeystoreFileIsMissing() {
        when(fileService.exists(any(Path.class))).thenReturn(false);

        // arrange
        SaveKeystoreRequestDto dtoInput = TestUtils.createSaveKeystoreRequestDto();
        dtoInput.setId(null);
        dtoInput.setGenerated(true);
        String model = TestUtils.objectMapper().writeValueAsString(dtoInput);

        when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dtoInput);
        when(converter.toNewEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());
        when(keystoreFileService.generate(any(SaveKeystoreRequestDto.class))).thenReturn("filename.jks");

        // act
        GmsException exception = assertThrows(GmsException.class, () -> service.save(model, null));

        // assert
        assertEquals("Keystore file does not exist!", exception.getMessage());
        verify(repository, never()).save(any());
        verify(converter).toNewEntity(any(), any());
        verify(objectMapper).readValue(eq(model), any(Class.class));
        verify(fileService).exists(any(Path.class));

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
        when(repository.countAllKeystoresByName(anyLong(), anyString())).thenReturn(1L);

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
        doThrow(new FileNotFoundException("Invalid")).when(fileService).createDirectories(any(Path.class));
        SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
        dto.setId(null);
        dto.setUserId(6L);
        String model = TestUtils.objectMapper().writeValueAsString(dto);

        MultipartFile multiPart = mock(MultipartFile.class);
        when(multiPart.getOriginalFilename()).thenReturn("test.jks");
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

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void shouldNotSaveNewEntityWithNonUniqueFileName() {
        String fileName = "my-key.jks";
        TestUtils.createDirectory("unit-test-output/1/");
        Path newFilePath = Files.createFile(Paths.get("unit-test-output/1/" + fileName));
        Files.writeString(newFilePath, "test");

        // arrange
        SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
        dto.setId(null);
        String model = TestUtils.objectMapper().writeValueAsString(dto);

        MultipartFile multiPart = mock(MultipartFile.class);
        when(multiPart.getOriginalFilename()).thenReturn(fileName);
        when(multiPart.getBytes()).thenReturn("test".getBytes());

        KeystoreEntity keystoreEntity = TestUtils.createKeystoreEntity();
        keystoreEntity.setFileName("my-key.jks");
        when(repository.save(any())).thenReturn(keystoreEntity);

        when(converter.toNewEntity(any(), eq(multiPart))).thenReturn(TestUtils.createKeystoreEntity());
        when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);
        when(fileService.exists(any(Path.class))).thenReturn(true);

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
        Files.createDirectory(Paths.get("unit-test-output/" + DemoData.USER_1_ID + "/"));
        SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
        dto.setId(null);
        String model = TestUtils.objectMapper().writeValueAsString(dto);

        MultipartFile multiPart = mock(MultipartFile.class);

        when(multiPart.getBytes()).thenReturn("test".getBytes());
        when(multiPart.getOriginalFilename()).thenReturn("test.jks");
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

        when(fileService.exists(any(Path.class))).thenReturn(true);
        when(fileService.readAllBytes(any(Path.class))).thenReturn("test".getBytes());
        when(converter.toEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());
        when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
        when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);
        when(repository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

        Files.createDirectory(Paths.get("unit-test-output/" + DemoData.USER_1_ID + "/"));

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
        dto.setAliases(List.of(new KeystoreAliasDto(1L, "alias", "test", AliasOperation.DELETE,
                EnabledAlgorithm.SHA256WITHRSA.getDisplayName())));
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
        dto.setGenerated(true);
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
        dto.getAliases().add(new KeystoreAliasDto(3L, "alias2", "test", AliasOperation.DELETE,
                EnabledAlgorithm.SHA256WITHRSA.getDisplayName()));
        dto.getAliases().add(new KeystoreAliasDto(4L, "alias3", "test", AliasOperation.SAVE,
                EnabledAlgorithm.SHA256WITHRSA.getDisplayName()));
        dto.setStatus(EntityStatus.DISABLED);
        dto.setId(1L);
        String model = TestUtils.objectMapper().writeValueAsString(dto);

        MultipartFile multiPart = mock(MultipartFile.class);
        when(multiPart.getOriginalFilename()).thenReturn("test.jks");
        when(multiPart.getBytes()).thenReturn("test".getBytes());

        when(converter.toEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());
        when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
        when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);
        when(repository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

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
        assertEquals(1L, (Long) capturedEvent.getMetadata().get("userId"));
        assertEquals(1L, (Long) capturedEvent.getMetadata().get("keystoreId"));
        assertEquals(EntityChangeType.KEYSTORE_DISABLED, capturedEvent.getType());
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void shouldSaveNewEntityWhenGeneratedInputIsAvailable() {
        AtomicInteger counter = new AtomicInteger(0);
        Files.createDirectory(Paths.get("unit-test-output/" + DemoData.USER_1_ID + "/"));
        String fileName = "generated-" + UUID.randomUUID() + ".jks";
        Path newFilePath = Files.createFile(Paths.get("temp-output/" + fileName));
        Files.writeString(newFilePath, "test");

        // arrange
        SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
        dto.setGenerated(true);
        dto.setId(null);
        String model = TestUtils.objectMapper().writeValueAsString(dto);

        when(converter.toNewEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());

        KeystoreEntity savedEntity = TestUtils.createKeystoreEntity();
        savedEntity.setFileName(fileName);
        when(repository.save(any())).thenReturn(savedEntity);
        when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);
        when(keystoreFileService.generate(any(SaveKeystoreRequestDto.class))).thenReturn(fileName);
        when(fileService.exists(any(Path.class))).thenAnswer((Answer<Boolean>) invocationOnMock -> counter.getAndIncrement() == 0);
        when(fileService.readAllBytes(any(Path.class))).thenReturn("test".getBytes());

        // act
        service.save(model, null);

        // assert
        verify(converter).toNewEntity(any(), any());
        verify(cryptoService).validateKeyStoreFile(any(SaveKeystoreRequestDto.class), any(byte[].class));
        verify(repository).save(any());
        verify(objectMapper).readValue(eq(model), any(Class.class));
        verify(fileService, times(2)).exists(any(Path.class));
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void shouldSaveEntityWithoutFileInputs() {
        // arrange
        when(fileService.readAllBytes(any(Path.class))).thenReturn("test".getBytes());
        when(fileService.exists(any(Path.class))).thenReturn(true);

        SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
        dto.getAliases().add(new KeystoreAliasDto(3L, "alias2", "test", AliasOperation.DELETE,
                EnabledAlgorithm.SHA256WITHRSA.getDisplayName()));
        dto.setStatus(EntityStatus.DISABLED);
        dto.setId(1L);
        dto.setGenerated(false);
        String model = TestUtils.objectMapper().writeValueAsString(dto);

        when(converter.toEntity(any(), any())).thenReturn(TestUtils.createKeystoreEntity());
        when(repository.save(any())).thenReturn(TestUtils.createKeystoreEntity());
        when(objectMapper.readValue(eq(model), any(Class.class))).thenReturn(dto);
        when(repository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

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
        assertEquals(1L, (Long) capturedEvent.getMetadata().get("userId"));
        assertEquals(1L, (Long) capturedEvent.getMetadata().get("keystoreId"));
        assertEquals(EntityChangeType.KEYSTORE_DISABLED, capturedEvent.getType());

        verify(fileService).readAllBytes(any(Path.class));
        verify(fileService).exists(any(Path.class));
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
        assertEquals(ENTITY_NOT_FOUND, exception.getMessage());
        verify(repository).findByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void shouldNotFindById() {
        // arrange
        when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // act
        GmsException exception = assertThrows(GmsException.class, () -> service.getById(1L));

        // assert
        assertEquals(ENTITY_NOT_FOUND, exception.getMessage());
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
        when(repository.findAllByUserId(anyLong(), any(Pageable.class)))
                .thenThrow(new RuntimeException("Unexpected error!"));

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
        when(converter.toDtoList(any())).thenReturn(KeystoreListDto.builder()
                .resultList(Lists.newArrayList(new KeystoreDto()))
                .totalElements(1).build());

        // act
        KeystoreListDto response = service.list(new PagingDto("ASC", "id", 0, 10));

        // assert
        assertNotNull(response);
        assertEquals(1, response.getResultList().size());
        verify(repository).findAllByUserId(anyLong(), any(Pageable.class));
        verify(converter).toDtoList(any());
    }

    @Test
    @SneakyThrows
    void shouldNotDeleteBecauseFileIsMissing() {
        // arrange
        doThrow(FileNotFoundException.class).when(fileService).delete(any(Path.class));
        when(repository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

        // act
        service.delete(11L);

        // assert
        TestUtils.assertLogContains(logAppender, "Keystore file cannot be deleted");
        verify(repository).findByIdAndUserId(anyLong(), anyLong());
        verify(aliasRepository).deleteByKeystoreId(anyLong());

        verify(fileService).delete(any(Path.class));
    }

    @Test
    @SneakyThrows
    void shouldDelete() {
        TestUtils.createDirectory("unit-test-output/1/");

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
        verify(aliasRepository).deleteByKeystoreId(anyLong());

        ArgumentCaptor<EntityChangeEvent> entityDisabledEventCaptor = ArgumentCaptor.forClass(EntityChangeEvent.class);
        verify(applicationEventPublisher).publishEvent(entityDisabledEventCaptor.capture());

        EntityChangeEvent capturedEvent = entityDisabledEventCaptor.getValue();
        assertEquals(1L, (Long) capturedEvent.getMetadata().get("userId"));
        assertEquals(1L, (Long) capturedEvent.getMetadata().get("keystoreId"));
        assertEquals(EntityChangeType.KEYSTORE_DELETED, capturedEvent.getType());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldToggleStatus(boolean enabled) {
        // arrange
        when(repository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

        // act
        service.toggleStatus(1L, enabled);

        // assert
        ArgumentCaptor<KeystoreEntity> argumentCaptor = ArgumentCaptor.forClass(KeystoreEntity.class);
        verify(repository).save(argumentCaptor.capture());
        KeystoreEntity capturedEntity = argumentCaptor.getValue();
        assertEquals(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED, capturedEntity.getStatus());
        verify(repository).findByIdAndUserId(anyLong(), anyLong());

        if (!enabled) {
            ArgumentCaptor<EntityChangeEvent> eventCaptor = ArgumentCaptor.forClass(EntityChangeEvent.class);
            verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
            EntityChangeEvent capturedEvent = eventCaptor.getValue();
            assertEquals(1L, capturedEvent.getMetadata().get("userId"));
            assertEquals(1L, capturedEvent.getMetadata().get("keystoreId"));
            assertEquals(EntityChangeType.KEYSTORE_DISABLED, capturedEvent.getType());
        }
    }

    @ParameterizedTest
    @MethodSource("valueData")
    void shouldGetValue(ValueHolder input) {
        // arrange
        when(repository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

        if (input.getAliasId() != null) {
            when(aliasRepository.findByIdAndKeystoreId(anyLong(), anyLong()))
                    .thenReturn(Optional.of(TestUtils.createKeystoreAliasEntity()));
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
        GmsException exception = assertThrows(GmsException.class, this::getValue);

        // assert
        assertEquals(ENTITY_NOT_FOUND, exception.getMessage());
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
        when(repository.getAllKeystoreNames(anyLong()))
                .thenReturn(Lists.newArrayList(new IdNamePairDto(1L, "keystore1"), new IdNamePairDto(1L, "keystore2")));

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
        when(aliasRepository.getAllAliasNames(anyLong()))
                .thenReturn(Lists.newArrayList(new IdNamePairDto(1L, "alias1"), new IdNamePairDto(1L, "alias2")));
        when(repository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(TestUtils.createKeystoreEntity()));

        // act
        IdNamePairListDto response = service.getAllKeystoreAliasNames(1L);

        // assert
        assertNotNull(response);
        assertEquals(2, response.getResultList().size());
        assertEquals("alias1", response.getResultList().getFirst().getName());
        verify(aliasRepository).getAllAliasNames(anyLong());
        verify(repository).findByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void shouldNotDownloadFile() {
        // arrange
        when(repository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
        when(fileService.readAllBytes(any(Path.class))).thenThrow(new IOException("File cannot be downloaded"));

        // act
        GmsException exception = assertThrows(GmsException.class, () -> service.downloadKeystore(1L));

        // assert
        assertEquals("java.io.IOException: File cannot be downloaded", exception.getMessage());
        verify(fileService).readAllBytes(any(Path.class));
    }

    @Test
    @SneakyThrows
    void shouldDownloadFile() {
        // arrange
        when(repository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(TestUtils.createKeystoreEntity()));
        when(fileService.readAllBytes(any(Path.class))).thenReturn("test".getBytes());

        // act
        DownloadFileResponseDto response = service.downloadKeystore(1L);

        // assert
        assertNotNull(response);
        assertEquals("test.jks", response.getFileName());
        assertEquals("test", new String(response.getFileContent()));
        verify(fileService).readAllBytes(any(Path.class));
    }

    public static List<ValueHolder> valueData() {
        return Lists.newArrayList(new ValueHolder(KeyStoreValueType.KEYSTORE_ALIAS, 1L, "test"),
                new ValueHolder(KeyStoreValueType.KEYSTORE_ALIAS_CREDENTIAL, 1L, "test"),
                new ValueHolder(KeyStoreValueType.KEYSTORE_CREDENTIAL, null, "test"));
    }
}
