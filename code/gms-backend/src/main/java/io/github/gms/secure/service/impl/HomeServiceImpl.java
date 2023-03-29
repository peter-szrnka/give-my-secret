package io.github.gms.secure.service.impl;

import io.github.gms.common.util.MdcUtils;
import io.github.gms.secure.dto.HomeDataResponseDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.service.AnnouncementService;
import io.github.gms.secure.service.ApiKeyService;
import io.github.gms.secure.service.EventService;
import io.github.gms.secure.service.HomeService;
import io.github.gms.secure.service.KeystoreService;
import io.github.gms.secure.service.SecretService;
import io.github.gms.secure.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class HomeServiceImpl implements HomeService {

    private final AnnouncementService announcementService;
    private final ApiKeyService apiKeyService;
    private final EventService eventService;
    private final KeystoreService keystoreService;
    private final SecretService secretService;
    private final UserService userService;

    public HomeServiceImpl(
            AnnouncementService announcementService,
            ApiKeyService apiKeyService,
            EventService eventService,
            KeystoreService keystoreService,
            SecretService secretService,
            UserService userService
    ) {
        this.announcementService = announcementService;
        this.apiKeyService = apiKeyService;
        this.eventService = eventService;
        this.keystoreService = keystoreService;
        this.secretService = secretService;
        this.userService = userService;
    }

    @Override
    public HomeDataResponseDto getHomeData() {
        HomeDataResponseDto dto = new HomeDataResponseDto();

        if (MdcUtils.isAdmin()) {
            dto.setAnnouncementCount(getValue(announcementService.count()));
            dto.setEvents(eventService.list(buildPaging("eventDate")));
            dto.setUserCount(getValue(userService.count()));
        } else {
            dto.setAnnouncements(announcementService.list(buildPaging("announcementDate")));
            dto.setApiKeyCount(getValue(apiKeyService.count()));
            dto.setKeystoreCount(getValue(keystoreService.count()));
            dto.setSecretCount(getValue(secretService.count()));
        }

        return dto;
    }

    private static PagingDto buildPaging(String property) {
        return PagingDto.builder()
                .direction("DESC").page(0).size(10).property(property)
                .build();
    }

    private static Long getValue(LongValueDto longValueDto) {
        return longValueDto.getValue();
    }
}
