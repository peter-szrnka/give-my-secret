package io.github.gms.functions.api;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.common.model.IpRestrictionPatterns;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.apikey.ApiKeyRepository;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import io.github.gms.functions.iprestriction.IpRestrictionValidator;
import io.github.gms.functions.secret.ApiKeyRestrictionRepository;
import io.github.gms.functions.secret.dto.GetSecretRequestDto;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static io.github.gms.util.TestUtils.createMockSecret;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link SecretPreparationService}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretPreparationServiceTest extends AbstractLoggingUnitTest {

    private static final GetSecretRequestDto dto = new GetSecretRequestDto("12345678", "123456");

    private SecretRepository secretRepository;
    private ApiKeyRepository apiKeyRepository;
    private UserRepository userRepository;
    private ApiKeyRestrictionRepository apiKeyRestrictionRepository;
    private IpRestrictionService ipRestrictionService;
    private IpRestrictionValidator ipRestrictionValidator;

    private SecretPreparationService service;

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
        service = new SecretPreparationService(secretRepository, apiKeyRepository, userRepository, apiKeyRestrictionRepository,
                ipRestrictionService, ipRestrictionValidator);
        addAppender(SecretPreparationService.class);
    }

    @Test
    void getSecretEntity_whenApiKeyMissing_thenReturnGmsException() {
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
    void getSecretEntity_whenUserMissing_thenReturnGmsException() {
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
    void getSecretEntity_whenSecretMissing_thenReturnGmsException() {
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
    void getSecretEntity_whenIpRestrictionConfigured_thenReturnGmsException() {
        // arrange
        when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
        when(userRepository.findById(anyLong())).thenReturn(createMockUser());
        when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL))));
        when(ipRestrictionService.checkIpRestrictionsBySecret(anyLong())).thenReturn(
                new IpRestrictionPatterns(List.of(IpRestrictionPattern.builder().allow(false).ipPattern(".*").build())));
        when(ipRestrictionValidator.isIpAddressBlocked(anyList())).thenReturn(true);

        // assert
        GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecretEntity(dto));
        assertEquals("You are not allowed to get this secret from your IP address!", exception.getMessage());

        verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
        verify(userRepository).findById(anyLong());
        verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
        verify(ipRestrictionService).checkIpRestrictionsBySecret(anyLong());
        verify(ipRestrictionValidator).isIpAddressBlocked(anyList());
    }

    @Test
    void sgetSecretEntity_whenApiKeyRestrictionConfigured_thenReturnGmsException() {
        // arrange
        when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
        when(userRepository.findById(anyLong())).thenReturn(createMockUser());
        when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL))));
        when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of(
                TestUtils.createApiKeyRestrictionEntity(2L),
                TestUtils.createApiKeyRestrictionEntity(3L)
        ));
        when(ipRestrictionService.checkIpRestrictionsBySecret(anyLong())).thenReturn(
                new IpRestrictionPatterns(List.of(IpRestrictionPattern.builder().allow(false).ipPattern(".*").build())));
        when(ipRestrictionValidator.isIpAddressBlocked(anyList())).thenReturn(false);


        // assert
        GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecretEntity(dto));
        assertEquals("You are not allowed to use this API key for this secret!", exception.getMessage());

        assertLogContains(logAppender, "You are not allowed to use this API key for this secret!");
        verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
        verify(userRepository).findById(anyLong());
        verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
        verify(apiKeyRestrictionRepository).findAllByUserIdAndSecretId(anyLong(), anyLong());
        verify(ipRestrictionService).checkIpRestrictionsBySecret(anyLong());
        verify(ipRestrictionValidator).isIpAddressBlocked(anyList());
    }

    @Test
    void getSecretEntity_whenApiKeyRestrictionsConfigured_thenSucceed() {
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
        when(ipRestrictionService.checkIpRestrictionsBySecret(anyLong())).thenReturn(
                new IpRestrictionPatterns(List.of(IpRestrictionPattern.builder().allow(false).ipPattern(".*").build())));
        when(ipRestrictionValidator.isIpAddressBlocked(anyList())).thenReturn(false);

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
        verify(ipRestrictionValidator).isIpAddressBlocked(anyList());
    }

    @Test
    void getSecretEntity_whenApiKeyRestrictionsMissing_thenSucceed() {
        // arrange
        SecretEntity mockSecretEntity = createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL);
        when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
        when(userRepository.findById(anyLong())).thenReturn(createMockUser());
        when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(mockSecretEntity)));
        when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of());
        when(ipRestrictionService.checkIpRestrictionsBySecret(anyLong())).thenReturn(
                new IpRestrictionPatterns(List.of(IpRestrictionPattern.builder().allow(false).ipPattern(".*").build())));
        when(ipRestrictionValidator.isIpAddressBlocked(anyList())).thenReturn(false);

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
        verify(ipRestrictionValidator).isIpAddressBlocked(anyList());
    }

    private static Optional<UserEntity> createMockUser() {
        return Optional.of(new UserEntity());
    }

    private static ApiKeyEntity createApiKeyEntity() {
        ApiKeyEntity mockApiKey = new ApiKeyEntity();
        mockApiKey.setId(1L);
        mockApiKey.setUserId(1L);
        mockApiKey.setValue("apikey");

        return mockApiKey;
    }
}
