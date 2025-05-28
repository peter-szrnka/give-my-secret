package io.github.gms.functions.setup;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.dto.SimpleResponseDto;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.systemproperty.SystemPropertyDto;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.functions.user.UserDto;
import io.github.gms.functions.user.UserService;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.gms.common.types.ErrorCode.GMS_003;
import static io.github.gms.common.util.Constants.ENTITY_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SetupServiceTest extends AbstractLoggingUnitTest {

    private UserService userService;
    private SystemAttributeRepository systemAttributeRepository;
    private SystemPropertyService systemPropertyService;
    private SetupService service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();

        userService = mock(UserService.class);
        systemAttributeRepository = mock(SystemAttributeRepository.class);
        systemPropertyService = mock(SystemPropertyService.class);
        service = new SetupService(userService, systemAttributeRepository, systemPropertyService);

        addAppender(SetupService.class);
    }

    @Test
    void stepBack_whenStatusIsNeedSetup_thenSkipStepBack() {
        // arrange
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_SETUP)));

        // act
        String result = service.stepBack();

        // assert
        assertNotNull(result);
        assertEquals(SystemStatus.NEED_SETUP.name(), result);
        verify(systemAttributeRepository).getSystemStatus();
    }

    @Test
    void stepBack_whenStatusIsOk_thenStepBack() {
        // arrange
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.OK)));

        // act
        String result = service.stepBack();

        // assert
        assertNotNull(result);
        assertEquals(SystemStatus.COMPLETE.name(), result);
        verify(systemAttributeRepository).getSystemStatus();
        verify(systemAttributeRepository).save(any());
    }

    @Test
    void getCurrentSuperAdmin_whenUserNotFound_thenFailAndReturnNull() {
        // arrange
        when(userService.getById(1L)).thenThrow(new GmsException(ENTITY_NOT_FOUND, GMS_003));

        // act
        UserDto result = service.getCurrentSuperAdmin();

        // assert
        assertNull(result);
        verify(userService).getById(1L);
    }

    @Test
    void getCurrentSuperAdmin_whenUserFound_thenReturnAdminUser() {
        // arrange
        when(userService.getById(1L)).thenReturn(TestUtils.createUserDto());

        // act
        UserDto result = service.getCurrentSuperAdmin();

        // assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService).getById(1L);
    }

    @Test
    void saveInitialStep_whenAllConditionsMet_thenProceed() {
        // arrange
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_SETUP)));
        when(systemAttributeRepository.save(any())).thenReturn(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_SETUP));

        // act
        SimpleResponseDto result = service.saveInitialStep();

        // assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        ArgumentCaptor<SystemAttributeEntity> captor = ArgumentCaptor.forClass(SystemAttributeEntity.class);
        verify(systemAttributeRepository).save(captor.capture());

        SystemAttributeEntity entity = captor.getValue();
        assertNotNull(entity);
        assertEquals(SystemStatus.NEED_ADMIN_USER.name(), entity.getValue());
        verify(systemAttributeRepository).getSystemStatus();
    }

    @Test
    void saveAdminUser_whenAllConditionsMet_thenProceed() {
        // arrange
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_ADMIN_USER)));
        when(systemAttributeRepository.save(any())).thenReturn(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_ADMIN_USER));
        when(userService.saveAdminUser(any())).thenReturn(new SaveEntityResponseDto(1L));

        // act
        SimpleResponseDto result = service.saveAdminUser(TestUtils.createSaveUserRequestDto());

        // assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        ArgumentCaptor<SystemAttributeEntity> captor = ArgumentCaptor.forClass(SystemAttributeEntity.class);
        verify(systemAttributeRepository).save(captor.capture());

        SystemAttributeEntity entity = captor.getValue();
        assertNotNull(entity);
        assertEquals(SystemStatus.NEED_AUTH_CONFIG.name(), entity.getValue());
        verify(systemAttributeRepository).getSystemStatus();
    }

    @Test
    void saveSystemProperties_whenNoSystemPropertiesSent_thenSkipUpdateStatus() {
        // arrange
        SetupSystemPropertiesDto dto = new SetupSystemPropertiesDto();
        List< SystemPropertyDto> properties = new ArrayList<>();
        dto.setProperties(properties);

        // act
        SimpleResponseDto result = service.saveSystemProperties(dto);

        // assert
        assertNotNull(result);
        assertTrue(result.isSuccess());

        verify(systemAttributeRepository, never()).save(any(SystemAttributeEntity.class));
        verify(systemAttributeRepository, never()).getSystemStatus();
    }

    @Test
    void saveSystemProperties_whenAllConditionsMet_thenProceed() {
        // arrange
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_AUTH_CONFIG)));
        when(systemAttributeRepository.save(any())).thenReturn(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_AUTH_CONFIG));

        SetupSystemPropertiesDto dto = new SetupSystemPropertiesDto();
        List< SystemPropertyDto> properties = new ArrayList<>();
        properties.add(new SystemPropertyDto());
        properties.add(SystemPropertyDto.builder().key("key").value("value").build());
        dto.setProperties(properties);

        // act
        SimpleResponseDto result = service.saveSystemProperties(dto);

        // assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        ArgumentCaptor<SystemAttributeEntity> captor = ArgumentCaptor.forClass(SystemAttributeEntity.class);
        verify(systemAttributeRepository).save(captor.capture());

        SystemAttributeEntity entity = captor.getValue();
        assertNotNull(entity);
        assertEquals(SystemStatus.NEED_ORG_DATA.name(), entity.getValue());
        verify(systemAttributeRepository).getSystemStatus();
        verify(systemPropertyService, times(1)).updateSystemProperty(any());
    }

    @Test
    void saveOrganizationData_whenAllConditionsMet_thenProceed() {
        // arrange
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_AUTH_CONFIG)));
        when(systemAttributeRepository.save(any())).thenReturn(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_AUTH_CONFIG));

        SetupSystemPropertiesDto dto = new SetupSystemPropertiesDto();
        List< SystemPropertyDto> properties = new ArrayList<>();
        properties.add(new SystemPropertyDto());
        properties.add(SystemPropertyDto.builder().key("key").value("value").build());
        dto.setProperties(properties);

        // act
        SimpleResponseDto result = service.saveOrganizationData(dto);

        // assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        ArgumentCaptor<SystemAttributeEntity> captor = ArgumentCaptor.forClass(SystemAttributeEntity.class);
        verify(systemAttributeRepository).save(captor.capture());

        SystemAttributeEntity entity = captor.getValue();
        assertNotNull(entity);
        assertEquals(SystemStatus.COMPLETE.name(), entity.getValue());
        verify(systemAttributeRepository).getSystemStatus();
        verify(systemPropertyService, times(1)).updateSystemProperty(any());
    }

    @Test
    void completeSetup_whenAllConditionsMet_thenProceed() {
        // arrange
        when(systemAttributeRepository.getSystemStatus()).thenReturn(Optional.of(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_ORG_DATA)));
        when(systemAttributeRepository.save(any())).thenReturn(TestUtils.createSystemAttributeEntity(SystemStatus.NEED_ORG_DATA));

        // act
        SimpleResponseDto result = service.completeSetup();

        // assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        ArgumentCaptor<SystemAttributeEntity> captor = ArgumentCaptor.forClass(SystemAttributeEntity.class);
        verify(systemAttributeRepository).save(captor.capture());

        SystemAttributeEntity entity = captor.getValue();
        assertNotNull(entity);
        assertEquals(SystemStatus.OK.name(), entity.getValue());
        verify(systemAttributeRepository).getSystemStatus();
    }
}
