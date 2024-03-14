package io.github.gms.functions.iprestriction;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.HttpUtils;
import io.github.gms.util.TestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static io.github.gms.common.util.HttpUtils.IP_WHITELISTED_LOCALHOST;
import static io.github.gms.util.TestUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class IpRestrictionServiceImplTest extends AbstractLoggingUnitTest {

    private IpRestrictionRepository repository;
    private IpRestrictionConverter converter;
    private HttpServletRequest httpServletRequest;
    private IpRestrictionServiceImpl service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        repository = mock(IpRestrictionRepository.class);
        converter = mock(IpRestrictionConverter.class);
        httpServletRequest = mock(HttpServletRequest.class);
        service = new IpRestrictionServiceImpl(repository, converter, httpServletRequest);

        ((Logger) LoggerFactory.getLogger(IpRestrictionServiceImpl.class)).addAppender(logAppender);
    }

    @Test
    void shouldUpdateIpRestrictionsForSecret() {
        // arrange
        List<IpRestrictionDto> restrictions = List.of(
                IpRestrictionDto.builder().allow(true).ipPattern(".*").build(), // existing
                IpRestrictionDto.builder().id(1L).allow(true).ipPattern("(127.0.0.)[0-9]{1,3}").build()
        );
        List<IpRestrictionEntity> mockEntities = List.of(
                IpRestrictionEntity.builder().id(1L).allow(true).ipPattern("(127.0.0.)[0-9]{1,3}").build(),
                IpRestrictionEntity.builder().id(2L).allow(true).ipPattern("(192.168.0.)[0-9]{1,3}").build()
        );
        when(repository.findAllBySecretId(1L)).thenReturn(mockEntities);
        when(converter.toEntity(any(IpRestrictionDto.class))).thenReturn(TestUtils.createIpRestriction());

        // act
        service.updateIpRestrictionsForSecret(1L, restrictions);

        // assert
        verify(repository).findAllBySecretId(1L);
        verify(converter, times(2)).toEntity(any(IpRestrictionDto.class));
        ArgumentCaptor<Set<Long>> argumentCaptorIds = ArgumentCaptor.forClass(Set.class);
        verify(repository).deleteAllById(argumentCaptorIds.capture());

        assertEquals(1, argumentCaptorIds.getValue().size());
        assertEquals(2L, argumentCaptorIds.getValue().iterator().next());
    }

    @Test
    void shouldReturnAllSecretById() {
        // arrange
        List<IpRestrictionEntity> mockEntities = List.of(
                IpRestrictionEntity.builder().id(1L).allow(true).ipPattern("(127.0.0.)[0-9]{1,3}").build()
        );
        when(repository.findAllBySecretId(1L)).thenReturn(mockEntities);
        when(converter.toDtoList(anyList())).thenReturn(List.of(
                IpRestrictionDto.builder().id(1L).allow(true).ipPattern("(127.0.0.)[0-9]{1,3}").build()
        ));

        // act
        List<IpRestrictionDto> response = service.getAllBySecretId(1L);

        // assert
        assertNotNull(response);
        verify(repository).findAllBySecretId(1L);
        verify(converter).toDtoList(anyList());
    }

    @ParameterizedTest
    @ValueSource(strings = {"127.0.0.1", "0:0:0:0:0:0:0:1"})
    void shouldCheckIpRestrictionsBySecretWithoutRules(String ipAddress) {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // arrange
            List<IpRestrictionEntity> mockEntities = List.of();
            when(repository.findAllBySecretId(1L)).thenReturn(mockEntities);
            when(converter.toModelList(anyList())).thenReturn(List.of());
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn(ipAddress);

            // act
            service.checkIpRestrictionsBySecret(1L);

            // assert
            verify(repository).findAllBySecretId(1L);
            verify(converter).toModelList(anyList());
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    @ParameterizedTest
    @MethodSource("restrictionInputData")
    void shouldFailWhenIpIsRestricted(boolean allow, String ipPattern, String ipAddress) {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // arrange
            List<IpRestrictionEntity> mockEntities = List.of();
            when(repository.findAllBySecretId(1L)).thenReturn(mockEntities);
            when(converter.toModelList(anyList())).thenReturn(List.of(
                    IpRestrictionPattern.builder()
                            .ipPattern(ipPattern)
                            .allow(allow)
                            .build()
            ));
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn(ipAddress);

            // act
            Exception exception = Assertions.assertThrows(GmsException.class, () -> service.checkIpRestrictionsBySecret(1L));

            // assert
            assertEquals("You are not allowed to get this secret from your IP address!", exception.getMessage());
            assertLogContains(logAppender, "Client IP address: " + ipAddress);
            verify(repository).findAllBySecretId(1L);
            verify(converter).toModelList(anyList());
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    @Test
    void shouldNotFailWhenIpIsWhitelisted() {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // arrange
            List<IpRestrictionEntity> mockEntities = List.of();
            when(repository.findAllBySecretId(1L)).thenReturn(mockEntities);
            when(converter.toModelList(anyList())).thenReturn(List.of(
                    IpRestrictionPattern.builder()
                            .ipPattern("(192.168.0.)[0-9]{1,3}")
                            .allow(true)
                            .build(),
                    IpRestrictionPattern.builder()
                            .ipPattern("(127.0.0.)[0-9]{1,3}")
                            .allow(false)
                            .build()
            ));
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn(IP_WHITELISTED_LOCALHOST);

            // act
            service.checkIpRestrictionsBySecret(1L);

            // assert
            assertLogContains(logAppender, "Client IP address: " + IP_WHITELISTED_LOCALHOST);
            verify(repository).findAllBySecretId(1L);
            verify(converter).toModelList(anyList());
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"127.0.0.1", "0:0:0:0:0:0:0:1"})
    void shouldCheckGlobalIpRestrictionsBySecretWithoutRules(String ipAddress) {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // arrange
            List<IpRestrictionEntity> mockEntities = List.of();
            when(repository.findAllGlobal()).thenReturn(mockEntities);
            when(converter.toModelList(anyList())).thenReturn(List.of());
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn(ipAddress);

            // act
            service.checkGlobalIpRestrictions();

            // assert
            verify(repository).findAllGlobal();
            verify(converter).toModelList(anyList());
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    private static Object[][] restrictionInputData() {
        return new Object[][]{
                {true, "(192.168.0.)[0-9]{1,3}", "127.0.0.1"},
                {false, "(192.168.0.)[0-9]{1,3}", "192.168.0.2"}
        };
    }
}
