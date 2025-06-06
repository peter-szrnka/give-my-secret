
package io.github.gms.functions.system;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.dto.VmOptionDto;
import io.github.gms.common.enums.ContainerHostType;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.functions.setup.SystemAttributeRepository;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static io.github.gms.common.util.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SystemServiceTest extends AbstractLoggingUnitTest {

    private Environment environment;
    private Clock clock;
    private SystemPropertyService systemPropertyService;
    private BuildProperties buildProperties;
    private SystemAttributeRepository systemAttributeRepository;
    private SystemService service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        addAppender(SystemService.class);

        environment = mock(Environment.class);
        clock = mock(Clock.class);
        buildProperties = mock(BuildProperties.class);
        systemPropertyService = mock(SystemPropertyService.class);
        systemAttributeRepository = mock(SystemAttributeRepository.class);

        service = new SystemService(environment, clock, systemPropertyService, systemAttributeRepository);
        service.setAuthType("db");
    }

    @Test
    void getVmOptions_whenCalled_thenReturnData() {
        // act
        List<VmOptionDto> vmOptions = service.getVmOptions();

        // assert
        assertNotNull(vmOptions);
        assertFalse(vmOptions.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"NEED_SETUP", OK})
    void getSystemStatus_whenInputProvided_thenReturnDto(String mockResponse) {
        // arrange
        service.setAuthType("db");
        service.setBuildProperties(buildProperties);
        when(clock.getZone()).thenReturn(ZoneId.of("Europe/Budapest"));
        when(buildProperties.getTime()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(systemAttributeRepository.getSystemStatus())
                .thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.valueOf(mockResponse))));

        // act
        SystemStatusDto response = service.getSystemStatus();

        // assert
        Assertions.assertEquals(mockResponse, response.getStatus());
        assertEquals("db", response.getAuthMode());
        assertEquals("2023-06-29T02:00:00.000+0200", response.getBuilt());
    }

    @ParameterizedTest
    @MethodSource("inputData")
    void getSystemStatus_whenCustomInputProvided_thenReturnOkWithDifferentAuthMethod(String selectedAuth, boolean hasBuildProperties, String expectedVersion, ContainerHostType containerHostType, String expectedContainerId) {
        // arrange
        service.setAuthType(selectedAuth);
        service.setBuildProperties(buildProperties);
        when(systemAttributeRepository.getSystemStatus())
                .thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

        when(environment.getProperty(CONTAINER_HOST_TYPE)).thenReturn(containerHostType.name());
        if (containerHostType == ContainerHostType.DOCKER || containerHostType == ContainerHostType.SWARM) {
            when(environment.getProperty(DOCKER_CONTAINER_ID, N_A)).thenReturn(expectedContainerId);
        } else if(containerHostType == ContainerHostType.KUBERNETES || containerHostType == ContainerHostType.OPENSHIFT) {
            when(environment.getProperty(POD_ID, N_A)).thenReturn(expectedContainerId);
        }

        if (hasBuildProperties) {
            when(clock.getZone()).thenReturn(ZoneId.of("Europe/Budapest"));
            when(buildProperties.getTime()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
            when(buildProperties.getVersion()).thenReturn("1.0.0");
        } else {
            service.setBuildProperties(null);
            when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
            when(clock.getZone()).thenReturn(ZoneId.of("Europe/Budapest"));
        }

        // act
        SystemStatusDto response = service.getSystemStatus();

        // assert
        assertEquals(OK, response.getStatus());
        assertEquals(expectedVersion, response.getVersion());
        assertEquals(containerHostType, response.getContainerHostType());
        assertEquals(expectedContainerId, response.getContainerId());
        verify(environment, times(2)).getProperty(CONTAINER_HOST_TYPE);

        if (containerHostType == ContainerHostType.DOCKER || containerHostType == ContainerHostType.SWARM) {
            verify(environment).getProperty(DOCKER_CONTAINER_ID, N_A);
        } else if(containerHostType == ContainerHostType.KUBERNETES || containerHostType == ContainerHostType.OPENSHIFT) {
            verify(environment).getProperty(POD_ID, N_A);
        }

        if (hasBuildProperties) {
            verify(buildProperties).getTime();
            verify(buildProperties).getVersion();
        }
    }

    @Test
    void getSystemStatus_whenAutomaticLogoutEnabled_thenReturnAutomaticLogoutData() {
        // arrange
        service.setBuildProperties(buildProperties);
        when(clock.getZone()).thenReturn(ZoneId.of("Europe/Budapest"));
        when(buildProperties.getTime()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(systemPropertyService.getBoolean(SystemProperty.ENABLE_AUTOMATIC_LOGOUT)).thenReturn(true);
        when(systemPropertyService.getInteger(SystemProperty.AUTOMATIC_LOGOUT_TIME_IN_MINUTES)).thenReturn(30);
        when(systemAttributeRepository.getSystemStatus())
                .thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

        // act
        SystemStatusDto response = service.getSystemStatus();

        // assert
        assertEquals(OK, response.getStatus());
        verify(systemPropertyService).getBoolean(SystemProperty.ENABLE_AUTOMATIC_LOGOUT);
        verify(systemPropertyService).getInteger(SystemProperty.AUTOMATIC_LOGOUT_TIME_IN_MINUTES);
    }

    public static Object[][] inputData() {
        return new Object[][]{
                {SELECTED_AUTH_LDAP, false, "DevRuntime", ContainerHostType.DOCKER, "dockerContainerId"},
                {SELECTED_AUTH_LDAP, false, "DevRuntime", ContainerHostType.SWARM, "swarmContainerId"},
                {SELECTED_AUTH_SSO, true, "1.0.0", ContainerHostType.KUBERNETES, "podId"},
                {SELECTED_AUTH_DB, true, "1.0.0", ContainerHostType.OPENSHIFT, "podId"},
                {SELECTED_AUTH_DB, true, "1.0.0", ContainerHostType.UNKNOWN, N_A},
        };
    }
}
