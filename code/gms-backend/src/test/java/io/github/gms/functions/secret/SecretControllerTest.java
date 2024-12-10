package io.github.gms.functions.secret;

import io.github.gms.abstraction.AbstractClientControllerTest;
import io.github.gms.common.dto.BooleanValueDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.functions.secret.dto.SaveSecretRequestDto;
import io.github.gms.functions.secret.dto.SecretDto;
import io.github.gms.functions.secret.dto.SecretListDto;
import io.github.gms.functions.secret.dto.SecretValueDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static io.github.gms.util.TestConstants.TEST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link SecretController}
 * 
 * @author Peter Szrnka
 */
class SecretControllerTest extends AbstractClientControllerTest<SecretService, SecretController> {

    private SecretRotationService secretRotationService;
    private SecretValueSizeValidatorService secretValueSizeValidatorService;

    @BeforeEach
    void setup() {
        service = Mockito.mock(SecretService.class);
        secretRotationService = mock(SecretRotationService.class);
        secretValueSizeValidatorService = mock(SecretValueSizeValidatorService.class);
        controller = new SecretController(service, secretRotationService, secretValueSizeValidatorService);
    }

    @Test
    void delete_whenInputProvided_thenReturnOk() {
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
    void toggle_whenInputProvided_thenReturnOk() {
        // act
        ResponseEntity<String> response = controller.toggle(1L, true);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).toggleStatus(1L,true);
    }

    @Test
    void save_whenInputProvided_thenReturnOk() {
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
    void getById_whenInputProvided_thenReturnOk() {
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
    void list_whenInputProvided_thenReturnOk() {
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
    void getValue_whenInputProvided_thenReturnOk() {
        // arrange
        when(service.getSecretValue(1L)).thenReturn(TEST);

        // act
        ResponseEntity<String> response = controller.getValue(1L);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(TEST, response.getBody());
        verify(service).getSecretValue(1L);
    }

    @Test
    void rotateSecret_whenInputProvided_thenReturnOk() {
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

    @Test
    void validateValueLength_whenInputProvided_thenReturnOk() {
        // arrange
        when(secretValueSizeValidatorService.validateValueLength(any())).thenReturn(new BooleanValueDto(true));

        // act
        ResponseEntity<BooleanValueDto> response = controller.validateValueLength(SecretValueDto.builder().build());

        // assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getValue());
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(secretValueSizeValidatorService).validateValueLength(any());
    }
}