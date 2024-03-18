package io.github.gms.functions.api;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.apikey.ApiKeyRepository;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import io.github.gms.functions.iprestriction.IpRestrictionValidator;
import io.github.gms.functions.secret.ApiKeyRestrictionRepository;
import io.github.gms.functions.secret.GetSecretRequestDto;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static io.github.gms.util.TestUtils.assertLogContains;
import static io.github.gms.util.TestUtils.createMockSecret;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link SecretPreparationServiceImpl}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretPreparationServiceImplTest extends AbstractLoggingUnitTest {

    private static final GetSecretRequestDto dto = new GetSecretRequestDto("12345678", "123456");

    private SecretRepository secretRepository;
    private ApiKeyRepository apiKeyRepository;
    private UserRepository userRepository;
    private ApiKeyRestrictionRepository apiKeyRestrictionRepository;
    private IpRestrictionService ipRestrictionService;
    private IpRestrictionValidator ipRestrictionValidator;

    private SecretPreparationServiceImpl service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        secretRepository = mock(SecretRepository.class);
        apiKeyRepository = mock(ApiKeyRepository.class);
        userRepository = mock(UserRepository.class);
        apiKeyRestrictionRepository = mock(ApiKeyRestrictionRepository.class);
        ipRestrictionService = mock(IpRestrictionService.class);
        ipRestrictionValidator = mock(IpRestrictionValidator.class);
        service = new SecretPreparationServiceImpl(secretRepository, apiKeyRepository, userRepository, apiKeyRestrictionRepository,
                ipRestrictionService, ipRestrictionValidator);
        ((Logger) LoggerFactory.getLogger(SecretPreparationServiceImpl.class)).addAppender(logAppender);
    }

    @Test
    void shouldApiKeyMissing() {
        // arrange
        when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(null);

        // assert
        GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecretEntity(dto));
        assertEquals("Wrong API key!", exception.getMessage());

        assertLogContains(logAppender, "API key not found");
        verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
        verify(userRepository, never()).findById(anyLong());
        verify(ipRestrictionService, never()).checkIpRestrictionsBySecret(anyLong());
    }

    @Test
    void shouldUserMissing() {
        // arrange
        when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());

        // assert
        GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecretEntity(dto));
        assertEquals("User not found!", exception.getMessage());

        assertLogContains(logAppender, "User not found");
        verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
        verify(userRepository).findById(anyLong());
        verify(ipRestrictionService, never()).checkIpRestrictionsBySecret(anyLong());
    }

    @Test
    void shouldSecretMissing() {
        // arrange
        when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
        when(userRepository.findById(anyLong())).thenReturn(createMockUser());
        when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn(Optional.empty());

        // assert
        GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecretEntity(dto));
        assertEquals("Secret is not available!", exception.getMessage());

        assertLogContains(logAppender, "Secret not found");
        verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
        verify(userRepository).findById(anyLong());
        verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
        verify(ipRestrictionService, never()).checkIpRestrictionsBySecret(anyLong());
    }

    @Test
    void shouldFailBecauseOfApiKeyRestriction() {
        // arrange
        when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
        when(userRepository.findById(anyLong())).thenReturn(createMockUser());
        when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL))));
        when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of(
                TestUtils.createApiKeyRestrictionEntity(2L),
                TestUtils.createApiKeyRestrictionEntity(3L)
        ));

        // assert
        GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecretEntity(dto));
        assertEquals("You are not allowed to use this API key for this secret!", exception.getMessage());

        assertLogContains(logAppender, "You are not allowed to use this API key for this secret!");
        verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
        verify(userRepository).findById(anyLong());
        verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
        verify(apiKeyRestrictionRepository).findAllByUserIdAndSecretId(anyLong(), anyLong());
        verify(ipRestrictionService).checkIpRestrictionsBySecret(anyLong());
    }

    @Test
    void shouldSucceedWithApiKeyRestrictions() {
        // arrange
        SecretEntity mockSecretEntity = createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL);
        when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
        when(userRepository.findById(anyLong())).thenReturn(createMockUser());
        when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(mockSecretEntity)));
        when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of(
                TestUtils.createApiKeyRestrictionEntity(1L),
                TestUtils.createApiKeyRestrictionEntity(2L),
                TestUtils.createApiKeyRestrictionEntity(3L)
        ));

        // act
        SecretEntity response = service.getSecretEntity(dto);

        // assert
        assertNotNull(response);
        assertEquals(mockSecretEntity, response);
        verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
        verify(userRepository).findById(anyLong());
        verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
        verify(apiKeyRestrictionRepository).findAllByUserIdAndSecretId(anyLong(), anyLong());
        verify(ipRestrictionService).checkIpRestrictionsBySecret(anyLong());
    }

    @Test
    void shouldSucceedWithoutApiKeyRestrictions() {
        // arrange
        SecretEntity mockSecretEntity = createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL);
        when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
        when(userRepository.findById(anyLong())).thenReturn(createMockUser());
        when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(mockSecretEntity)));
        when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of());

        // act
        SecretEntity response = service.getSecretEntity(dto);

        //assert
        assertNotNull(response);
        assertEquals(mockSecretEntity, response);
        verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
        verify(userRepository).findById(anyLong());
        verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
        verify(apiKeyRestrictionRepository).findAllByUserIdAndSecretId(anyLong(), anyLong());
        verify(ipRestrictionService).checkIpRestrictionsBySecret(anyLong());
    }

    /*@Test
    void shouldProceedWithWhitelistedIp() {
        // arrange
        SecretEntity mockSecretEntity = createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL);
        when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
        when(userRepository.findById(anyLong())).thenReturn(createMockUser());
        when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(mockSecretEntity)));
        when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of());

        // act
        SecretEntity response = service.getSecretEntity(dto);

        //assert
        assertNotNull(response);
        assertEquals(mockSecretEntity, response);
        verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
        verify(userRepository).findById(anyLong());
        verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
        verify(apiKeyRestrictionRepository).findAllByUserIdAndSecretId(anyLong(), anyLong());
        verify(ipRestrictionService).checkIpRestrictionsBySecret(anyLong());
    }*/

    /*@ParameterizedTest
    @MethodSource("restrictionInputData")
    void shouldFailWhenIpIsRestricted(boolean allow, String ipPattern, String ipAddress) {

        // arrange
        SecretEntity mockSecretEntity = createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL);
        when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
        when(userRepository.findById(anyLong())).thenReturn(createMockUser());
        when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(mockSecretEntity)));

        // act
        GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecretEntity(dto));

        //assert
        assertEquals("You are not allowed to get this secret from your IP address!", exception.getMessage());
        assertLogContains(logAppender, "Client IP address: " + ipAddress);
        verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
        verify(userRepository).findById(anyLong());
        verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
        verify(ipRestrictionService).checkIpRestrictionsBySecret(anyLong());
    }*/

    private static Optional<UserEntity> createMockUser() {
        UserEntity entity = new UserEntity();
        return Optional.of(entity);
    }

    private static ApiKeyEntity createApiKeyEntity() {
        ApiKeyEntity mockApiKey = new ApiKeyEntity();
        mockApiKey.setId(1L);
        mockApiKey.setUserId(1L);
        mockApiKey.setValue("apikey");

        return mockApiKey;
    }
}
