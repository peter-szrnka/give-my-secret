package io.github.gms.secure.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.gms.secure.dto.HomeDataResponseDto;
import io.github.gms.secure.service.HomeService;

/**
 * Unit test of {@link HomeController}
 * 
 * @author Peter Szrnka
 */
class HomeControllerTest {

    protected HomeController controller;
    protected HomeService service;

    @BeforeEach
    void setupTest() {
        service = Mockito.mock(HomeService.class);
        controller = new HomeController(service);
    }

    @Test
    void shouldDeleteEntity() {
        // arrange
        HomeDataResponseDto dto = new HomeDataResponseDto();
        dto.setAnnouncementCount(0L);
        dto.setApiKeyCount(0L);
        dto.setKeystoreCount(0L);
        dto.setSecretCount(0L);
        when(service.getHomeData()).thenReturn(dto);

        // act
        HomeDataResponseDto response = controller.getHomeData();

        // assert
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getHomeData();
    }
}