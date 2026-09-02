package io.github.gms.functions.apikey;

import io.github.gms.abstraction.AbstractClientControllerTest;
import io.github.gms.common.dto.IdNamePairDto;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static io.github.gms.util.TestConstants.TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link ApiKeyController}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiKeyControllerTest extends AbstractClientControllerTest<ApiKeyService, ApiKeyController> {

    @BeforeEach
    void setup() {
        service = mock(ApiKeyService.class);
        controller = new ApiKeyController(service);
    }

    @Test
    void delete_whenInputProvided_thenReturnOk() {
        // when
        ResponseEntity<String> response = controller.delete(1L);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).delete(1L);
    }

    @Test
    void toggle_whenInputProvided_thenReturnOk() {
        // when
        ResponseEntity<String> response = controller.toggle(1L, true);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).toggleStatus(1L,true);
    }

    @Test
    void save_whenInputProvided_thenReturnOk() {
        // given
        SaveApiKeyRequestDto dto = TestUtils.createNewSaveApiKeyRequestDto();
        when(service.save(dto)).thenReturn(new SaveEntityResponseDto(2L));

        // when
        SaveEntityResponseDto response = controller.save(dto);

        // then
        assertNotNull(response);
        assertEquals(2L, response.getEntityId());
        verify(service).save(dto);
    }

    @Test
    void getById_whenInputProvided_thenReturnOk() {
        // given
        ApiKeyDto dto = TestUtils.createApiKeyDto();
        when(service.getById(1L)).thenReturn(dto);

        // when
        ApiKeyDto response = controller.getById(1L);

        // then
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getById(1L);
    }

    @Test
    void list_whenInputProvided_thenReturnOk() {
        // given
        ApiKeyListDto dtoList = TestUtils.createApiKeyListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);

        // when
        ApiKeyListDto response = controller.list(
                "DESC",
                "id",
                0,
                10
        );

        // then
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pageable);
    }

    @Test
    void getValue_whenInputProvided_thenReturnOk() {
        // given
        when(service.getDecryptedValue(1L)).thenReturn(TEST);

        // when
        String response = controller.getValue(1L);

        // then
        assertNotNull(response);
        assertEquals(TEST, response);
        verify(service).getDecryptedValue(1L);
    }

    @Test
    void getAllApiKeyNames_whenInputProvided_thenReturnOk() {
        // given
        IdNamePairListDto mock = new IdNamePairListDto();
        mock.setResultList(List.of(new IdNamePairDto()));
        when(service.getAllApiKeyNames()).thenReturn(mock);

        // when
        IdNamePairListDto response = controller.getAllApiKeyNames();

        // then
        assertNotNull(response);
        assertEquals(1, response.getResultList().size());
        verify(service).getAllApiKeyNames();
    }
}
