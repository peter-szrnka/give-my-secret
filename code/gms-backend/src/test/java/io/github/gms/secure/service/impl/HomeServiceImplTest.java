package io.github.gms.secure.service.impl;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.secure.dto.EventDto;
import io.github.gms.secure.dto.EventListDto;
import io.github.gms.secure.dto.HomeDataResponseDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.service.AnnouncementService;
import io.github.gms.secure.service.ApiKeyService;
import io.github.gms.secure.service.EventService;
import io.github.gms.secure.service.KeystoreService;
import io.github.gms.secure.service.SecretService;
import io.github.gms.secure.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class HomeServiceImplTest extends AbstractUnitTest {

    private AnnouncementService announcementService;
    private ApiKeyService apiKeyService;
    private EventService eventService;
    private KeystoreService keystoreService;
    private SecretService secretService;
    private UserService userService;

    private HomeServiceImpl service;

    @BeforeEach
    public void setup() {
        announcementService = mock(AnnouncementService.class);
        apiKeyService = mock(ApiKeyService.class);
        eventService = mock(EventService.class);
        keystoreService = mock(KeystoreService.class);
        secretService = mock(SecretService.class);
        userService = mock(UserService.class);
        service = new HomeServiceImpl(announcementService, apiKeyService, eventService, keystoreService, secretService, userService);
    }

    @Test
    void shouldReturnAdminData() {
        // arrange
        MDC.put(MdcParameter.IS_ADMIN.getDisplayName(), "true");
        when(announcementService.count()).thenReturn(new LongValueDto(2L));
        when(userService.count()).thenReturn(new LongValueDto(5L));
        when(eventService.list(any(PagingDto.class))).thenReturn(EventListDto.builder().totalElements(1)
                .resultList(List.of(new EventDto())).build());

        // act
        HomeDataResponseDto response = service.getHomeData();

        // assert
        assertNotNull(response);
        assertEquals(2L, response.getAnnouncementCount());
        assertEquals(5L, response.getUserCount());

        verify(announcementService).count();
        verify(userService).count();
        verify(eventService).list(any(PagingDto.class));
    }

    @Test
    void shouldReturnUserData() {
        // arrange
        MDC.put(MdcParameter.IS_ADMIN.getDisplayName(), "false");
        when(apiKeyService.count()).thenReturn(new LongValueDto(4L));
        when(keystoreService.count()).thenReturn(new LongValueDto(2L));
        when(secretService.count()).thenReturn(new LongValueDto(3L));

        // act
        HomeDataResponseDto response = service.getHomeData();

        // assert
        assertNotNull(response);
        assertEquals(4L, response.getApiKeyCount());
        assertEquals(2L, response.getKeystoreCount());
        assertEquals(3L, response.getSecretCount());

        verify(apiKeyService).count();
        verify(keystoreService).count();
        verify(secretService).count();
    }
}
