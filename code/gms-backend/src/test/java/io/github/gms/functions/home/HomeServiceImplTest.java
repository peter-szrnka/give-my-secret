package io.github.gms.functions.home;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.functions.announcement.AnnouncementDto;
import io.github.gms.functions.announcement.AnnouncementListDto;
import io.github.gms.functions.event.EventDto;
import io.github.gms.functions.event.EventListDto;
import io.github.gms.functions.home.HomeServiceImpl;
import io.github.gms.functions.home.HomeDataResponseDto;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.dto.PagingDto;
import io.github.gms.functions.announcement.AnnouncementService;
import io.github.gms.functions.apikey.ApiKeyService;
import io.github.gms.functions.event.EventService;
import io.github.gms.functions.keystore.KeystoreService;
import io.github.gms.functions.secret.SecretService;
import io.github.gms.functions.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
        assertEquals(1L, response.getEvents().getResultList().size());

        verify(announcementService).count();
        verify(userService).count();
        ArgumentCaptor<PagingDto> captor = ArgumentCaptor.forClass(PagingDto.class);
        verify(eventService).list(captor.capture());
        PagingDto captured = captor.getValue();
        assertEquals("PagingDto(direction=DESC, property=eventDate, page=0, size=10)", captured.toString());
    }

    @Test
    void shouldReturnUserData() {
        // arrange
        MDC.put(MdcParameter.IS_ADMIN.getDisplayName(), "false");
        when(apiKeyService.count()).thenReturn(new LongValueDto(4L));
        when(keystoreService.count()).thenReturn(new LongValueDto(2L));
        when(secretService.count()).thenReturn(new LongValueDto(3L));
        when(announcementService.list(any(PagingDto.class))).thenReturn(AnnouncementListDto.builder().totalElements(1)
                .resultList(List.of(new AnnouncementDto())).build());

        // act
        HomeDataResponseDto response = service.getHomeData();

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getAnnouncements().getTotalElements());
        assertEquals(4L, response.getApiKeyCount());
        assertEquals(2L, response.getKeystoreCount());
        assertEquals(3L, response.getSecretCount());

        verify(apiKeyService).count();
        verify(keystoreService).count();
        verify(secretService).count();

        ArgumentCaptor<PagingDto> captor = ArgumentCaptor.forClass(PagingDto.class);
        verify(announcementService).list(captor.capture());
        PagingDto captured = captor.getValue();
        assertEquals("PagingDto(direction=DESC, property=announcementDate, page=0, size=10)", captured.toString());
    }
}
