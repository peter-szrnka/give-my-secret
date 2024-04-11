package io.github.gms.functions.home;

import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.common.util.MdcUtils;
import io.github.gms.functions.announcement.AnnouncementService;
import io.github.gms.functions.apikey.ApiKeyService;
import io.github.gms.functions.event.EventService;
import io.github.gms.functions.keystore.KeystoreService;
import io.github.gms.functions.secret.SecretService;
import io.github.gms.functions.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class HomeService {

    private final AnnouncementService announcementService;
    private final ApiKeyService apiKeyService;
    private final EventService eventService;
    private final KeystoreService keystoreService;
    private final SecretService secretService;
    private final UserService userService;

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

    private static Pageable buildPaging(String property) {
        return ConverterUtils.createPageable("DESC", property, 0, 10);
    }

    private static Long getValue(LongValueDto longValueDto) {
        return longValueDto.getValue();
    }
}
