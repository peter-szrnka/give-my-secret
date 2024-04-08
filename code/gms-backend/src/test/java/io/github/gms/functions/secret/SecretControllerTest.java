package io.github.gms.functions.secret;

import io.github.gms.abstraction.AbstractClientControllerTest;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link SecretController}
 * 
 * @author Peter Szrnka
 */
class SecretControllerTest extends AbstractClientControllerTest<SecretService, SecretController> {

    private SecretRotationService secretRotationService;

    @BeforeEach
    void setupTest() {
        service = Mockito.mock(SecretService.class);
        secretRotationService = mock(SecretRotationService.class);
        controller = new SecretController(service, secretRotationService);
    }

    @Test
    void shouldDeleteEntity() {
        // arrange
        doNothing().when(service).delete(1L);

        // act
        ResponseEntity<String> response = controller.delete(1L);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).delete(1L);
    }

    @Test
    void shouldToggleEntityStatus() {
        // act
        ResponseEntity<String> response = controller.toggle(1L, true);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).toggleStatus(1L,true);
    }

    @Test
    void shouldSave() {
        // arrange
        SaveSecretRequestDto dto = TestUtils.createSaveSecretRequestDto(2L);
        when(service.save(dto)).thenReturn(new SaveEntityResponseDto(2L));

        // act
        SaveEntityResponseDto response = controller.save(dto);

        // assert
        assertNotNull(response);
        assertEquals(2L, response.getEntityId());
        verify(service).save(dto);
    }

    @Test
    void shouldReturnById() {
        // arrange
        SecretDto dto = TestUtils.createSecretDto();
        when(service.getById(1L)).thenReturn(dto);

        // act
        SecretDto response = controller.getById(1L);

        // assert
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getById(1L);
    }

    @Test
    void shouldReturnList() {
        // arrange
        SecretListDto dtoList = TestUtils.createSecretListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);

        // act
        SecretListDto response = controller.list(
                "DESC",
                "id",
                0,
                10
        );

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pageable);
    }

    @Test
    void shouldReturnValue() {
        // arrange
        when(service.getSecretValue(1L)).thenReturn("test");

        // act
        ResponseEntity<String> response = controller.getValue(1L);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("test", response.getBody());
        verify(service).getSecretValue(1L);
    }

    @Test
    void shouldRotateSecret() {
        // arrange
        doNothing().when(secretRotationService).rotateSecretById(1L);

        // act
        ResponseEntity<String> response = controller.rotateSecret(1L);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(secretRotationService).rotateSecretById(1L);
    }
}