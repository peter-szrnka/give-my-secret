package io.github.gms.functions.api;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.SecretType;
import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.HttpUtils;
import io.github.gms.functions.apikey.ApiKeyEntity;
import io.github.gms.functions.apikey.ApiKeyRepository;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import io.github.gms.functions.secret.ApiKeyRestrictionRepository;
import io.github.gms.functions.secret.GetSecretRequestDto;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.TestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static io.github.gms.util.TestUtils.assertLogContains;
import static io.github.gms.util.TestUtils.createMockSecret;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link SecretPreparationServiceImpl}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretPreparationServiceImplTest extends AbstractUnitTest {

    private static final GetSecretRequestDto dto = new GetSecretRequestDto("12345678", "123456");

    private ListAppender<ILoggingEvent> logAppender;

    private SecretRepository secretRepository;
    private ApiKeyRepository apiKeyRepository;
    private UserRepository userRepository;
    private ApiKeyRestrictionRepository apiKeyRestrictionRepository;
    private IpRestrictionService ipRestrictionService;
    private HttpServletRequest httpServletRequest;

    private SecretPreparationServiceImpl service;

    @BeforeEach
    void beforeEach() {
		secretRepository = mock(SecretRepository.class);
		apiKeyRepository = mock(ApiKeyRepository.class);
		userRepository = mock(UserRepository.class);
		apiKeyRestrictionRepository = mock(ApiKeyRestrictionRepository.class);
        ipRestrictionService = mock(IpRestrictionService.class);
        httpServletRequest = mock(HttpServletRequest.class);
        service = new SecretPreparationServiceImpl(secretRepository, apiKeyRepository, userRepository, apiKeyRestrictionRepository,
                ipRestrictionService, httpServletRequest);

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
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // arrange
            when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
            when(userRepository.findById(anyLong())).thenReturn(createMockUser());
            when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL))));
            when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of(
                    TestUtils.createApiKeyRestrictionEntity(2L),
                    TestUtils.createApiKeyRestrictionEntity(3L)
            ));
            when(ipRestrictionService.getIpRestrictionsBySecret(anyLong())).thenReturn(emptyList());
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn("127.0.0.1");

            // assert
            GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecretEntity(dto));
            assertEquals("You are not allowed to use this API key for this secret!", exception.getMessage());

            assertLogContains(logAppender, "You are not allowed to use this API key for this secret!");
            verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
            verify(userRepository).findById(anyLong());
            verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
            verify(apiKeyRestrictionRepository).findAllByUserIdAndSecretId(anyLong(), anyLong());
            verify(ipRestrictionService).getIpRestrictionsBySecret(anyLong());
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    @Test
    void shouldSucceedWithApiKeyRestrictions() {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
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
            when(ipRestrictionService.getIpRestrictionsBySecret(anyLong())).thenReturn(emptyList());
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn("127.0.0.1");

            // act
            SecretEntity response = service.getSecretEntity(dto);

            // assert
            assertNotNull(response);
            assertEquals(mockSecretEntity, response);
            verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
            verify(userRepository).findById(anyLong());
            verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
            verify(apiKeyRestrictionRepository).findAllByUserIdAndSecretId(anyLong(), anyLong());
            verify(ipRestrictionService).getIpRestrictionsBySecret(anyLong());
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    @Test
    void shouldSucceedWithoutApiKeyRestrictions() {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // arrange
            SecretEntity mockSecretEntity = createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL);
            when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
            when(userRepository.findById(anyLong())).thenReturn(createMockUser());
            when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(mockSecretEntity)));
            when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of());
            when(ipRestrictionService.getIpRestrictionsBySecret(anyLong())).thenReturn(emptyList());
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn("127.0.0.1");

            // act
            SecretEntity response = service.getSecretEntity(dto);

            //assert
            assertNotNull(response);
            assertEquals(mockSecretEntity, response);
            verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
            verify(userRepository).findById(anyLong());
            verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
            verify(apiKeyRestrictionRepository).findAllByUserIdAndSecretId(anyLong(), anyLong());
            verify(ipRestrictionService).getIpRestrictionsBySecret(anyLong());
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    @Test
    void shouldProceedWithWhitelistedIp() {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // arrange
            SecretEntity mockSecretEntity = createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL);
            when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
            when(userRepository.findById(anyLong())).thenReturn(createMockUser());
            when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(mockSecretEntity)));
            when(apiKeyRestrictionRepository.findAllByUserIdAndSecretId(anyLong(), anyLong())).thenReturn(List.of());
            when(ipRestrictionService.getIpRestrictionsBySecret(anyLong()))
                    .thenReturn(List.of(IpRestrictionPattern.builder().ipPattern(".*").allow(true).build()));
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn("0:0:0:0:0:0:0:1");

            // act
            SecretEntity response = service.getSecretEntity(dto);

            //assert
            assertNotNull(response);
            assertEquals(mockSecretEntity, response);
            verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
            verify(userRepository).findById(anyLong());
            verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
            verify(apiKeyRestrictionRepository).findAllByUserIdAndSecretId(anyLong(), anyLong());
            verify(ipRestrictionService).getIpRestrictionsBySecret(anyLong());
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    @ParameterizedTest
    @MethodSource("restrictionInputData")
    void shouldFailWhenIpIsRestricted(boolean allow, String ipPattern, String ipAddress) {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // arrange
            SecretEntity mockSecretEntity = createMockSecret("encrypted", false, SecretType.SIMPLE_CREDENTIAL);
            when(apiKeyRepository.findByValueAndStatus(anyString(), any(EntityStatus.class))).thenReturn(createApiKeyEntity());
            when(userRepository.findById(anyLong())).thenReturn(createMockUser());
            when(secretRepository.findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE))).thenReturn((Optional.of(mockSecretEntity)));
            when(ipRestrictionService.getIpRestrictionsBySecret(anyLong()))
                    .thenReturn(List.of(IpRestrictionPattern.builder().ipPattern(ipPattern).allow(allow).build()));
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn(ipAddress);

            // act
            GmsException exception = Assertions.assertThrows(GmsException.class, () -> service.getSecretEntity(dto));

            //assert
            assertEquals("You are not allowed to get this secret from your IP address!", exception.getMessage());
            assertLogContains(logAppender, "Client IP address: " + ipAddress);
            verify(apiKeyRepository).findByValueAndStatus(anyString(), any(EntityStatus.class));
            verify(userRepository).findById(anyLong());
            verify(secretRepository).findByUserIdAndSecretIdAndStatus(anyLong(), anyString(), eq(EntityStatus.ACTIVE));
            verify(ipRestrictionService).getIpRestrictionsBySecret(anyLong());
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    private static Object[][] restrictionInputData() {
        return new Object[][] {
                { true, "(192.168.0)[0-9]{1,3}", "127.0.0.1" },
                { false, "(192.168.0)[0-9]{1,3}", "192.168.0.2" }
        };
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
