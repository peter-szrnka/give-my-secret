package io.github.gms.api.service.impl;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.GetSecretRequestDto;
import io.github.gms.secure.entity.ApiKeyEntity;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.secure.repository.ApiKeyRepository;
import io.github.gms.secure.repository.ApiKeyRestrictionRepository;
import io.github.gms.secure.repository.SecretRepository;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static io.github.gms.util.TestUtils.assertLogContains;
import static io.github.gms.util.TestUtils.createMockSecret;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class SecretPreparationServiceImplTest extends AbstractUnitTest {

    private static final GetSecretRequestDto dto = new GetSecretRequestDto("12345678", "123456");

    private ListAppender<ILoggingEvent> logAppender;

    private SecretRepository secretRepository;
    private ApiKeyRepository apiKeyRepository;
    private UserRepository userRepository;
    private ApiKeyRestrictionRepository apiKeyRestrictionRepository;

    private SecretPreparationServiceImpl service;

    @BeforeEach
    void beforeEach() {
		secretRepository = mock(SecretRepository.class);
		apiKeyRepository = mock(ApiKeyRepository.class);
		userRepository = mock(UserRepository.class);
		apiKeyRestrictionRepository = mock(ApiKeyRestrictionRepository.class);
        service = new SecretPreparationServiceImpl(secretRepository, apiKeyRepository, userRepository, apiKeyRestrictionRepository);

        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(SecretPreparationServiceImpl.class)).addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        logAppender.list.clear();
        logAppender.stop();
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
    }

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
