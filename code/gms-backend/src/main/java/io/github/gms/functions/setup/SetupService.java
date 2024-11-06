package io.github.gms.functions.setup;

import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.dto.SimpleResponseDto;
import io.github.gms.common.enums.SystemStatus;
import io.github.gms.common.types.ErrorCode;
import io.github.gms.common.types.GmsException;
import io.github.gms.functions.systemproperty.SystemPropertyDto;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.functions.user.SaveUserRequestDto;
import io.github.gms.functions.user.UserDto;
import io.github.gms.functions.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO;
import static io.github.gms.common.util.Constants.ENTITY_NOT_FOUND;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile(CONFIG_AUTH_TYPE_NOT_KEYCLOAK_SSO)
public class SetupService {

    private final UserService userService;
    private final SystemAttributeRepository systemAttributeRepository;
    private final SystemPropertyService systemPropertyService;

    public Map<String, String> getVmOptions() {
        Map<String, String> vmOptions = new HashMap<>();
        System.getProperties().forEach((key, value) -> vmOptions.put(key.toString(), value.toString()));
        return vmOptions;
    }

    public String stepBack() {
        SystemAttributeEntity systemStatusEntity = getCurrentSystemStatus();

        SystemStatus systemStatus = SystemStatus.valueOf(systemStatusEntity.getValue());
        SystemStatus previousStatus = systemStatus.getPreviousStatus();

        if (previousStatus == null) {
            return SystemStatus.NEED_SETUP.name();
        }

        String newStatus = previousStatus.name();
        systemStatusEntity.setValue(newStatus);
        systemAttributeRepository.save(systemStatusEntity);

        return newStatus;
    }

    public UserDto getCurrentSuperAdmin() {
        try {
            return userService.getById(1L);
        } catch (GmsException e) {
            return null;
        }
    }

    public SimpleResponseDto saveInitialStep() {
        updateSystemStatus(SystemStatus.NEED_ADMIN_USER);
        return SimpleResponseDto.builder().success(true).build();
    }

    public SaveEntityResponseDto saveAdminUser(@RequestBody SaveUserRequestDto dto) {
        SaveEntityResponseDto saveEntityResponseDto = userService.saveAdminUser(dto);
        updateSystemStatus(SystemStatus.NEED_AUTH_CONFIG);
        return saveEntityResponseDto;
    }

    public SimpleResponseDto saveSystemProperties(SetupSystemPropertiesDto dto) {
        return processSystemProperties(dto, SystemStatus.NEED_ORG_DATA);
    }

    public SimpleResponseDto saveOrganizationData(SetupSystemPropertiesDto dto) {
        return processSystemProperties(dto, SystemStatus.COMPLETE);
    }

    public SimpleResponseDto completeSetup() {
        updateSystemStatus(SystemStatus.OK);
        return SimpleResponseDto.builder().success(true).build();
    }

    private SimpleResponseDto processSystemProperties(SetupSystemPropertiesDto dto, SystemStatus newStatus) {
        for (SystemPropertyDto systemPropertyDto : dto.getProperties()) {
            if (systemPropertyDto.getValue() == null) {
                continue;
            }

            systemPropertyService.updateSystemProperty(systemPropertyDto);
        }

        if (!dto.getProperties().isEmpty()) {
            updateSystemStatus(newStatus);
        }

        return SimpleResponseDto.builder().success(true).build();
    }

    private void updateSystemStatus(SystemStatus systemStatus) {
        SystemAttributeEntity systemStatusEntity = getCurrentSystemStatus();
        systemStatusEntity.setValue(systemStatus.name());
        systemAttributeRepository.save(systemStatusEntity);
    }

    private SystemAttributeEntity getCurrentSystemStatus() {
        return systemAttributeRepository.getSystemStatus()
                .orElseThrow(() -> new GmsException(ENTITY_NOT_FOUND, ErrorCode.GMS_002));
    }
}
