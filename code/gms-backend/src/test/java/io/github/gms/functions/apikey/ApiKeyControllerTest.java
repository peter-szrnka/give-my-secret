package io.github.gms.functions.apikey;

import io.github.gms.common.dto.IdNamePairDto;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.abstraction.AbstractClientControllerTest;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link ApiKeyController}
 * 
 * @author Peter Szrnka
 */
class ApiKeyControllerTest extends AbstractClientControllerTest<ApiKeyService, ApiKeyController> {

    @BeforeEach
    void setupTest() {
        service = Mockito.mock(ApiKeyService.class);
        controller = new ApiKeyController(service);
    }

    @Test
    void shouldDeleteEntity() {

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
        SaveApiKeyRequestDto dto = TestUtils.createNewSaveApiKeyRequestDto();
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
        ApiKeyDto dto = TestUtils.createApiKeyDto();
        when(service.getById(1L)).thenReturn(dto);

        // act
        ApiKeyDto response = controller.getById(1L);

        // assert
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getById(1L);
    }

    @Test
    void shouldReturnList() {
        // arrange
        PagingDto pagingDto = new PagingDto("DESC", "id", 0, 10);
        ApiKeyListDto dtoList = TestUtils.createApiKeyListDto();
        when(service.list(pagingDto)).thenReturn(dtoList);
        

        // act
        ApiKeyListDto response = controller.list(pagingDto);

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pagingDto);
    }

    @Test
    void shouldReturnValue() {
        // arrange
        when(service.getDecryptedValue(1L)).thenReturn("test");

        // act
        String response = controller.getValue(1L);

        // assert
        assertNotNull(response);
        assertEquals("test", response);
        verify(service).getDecryptedValue(1L);
    }

    @Test
    void shouldReturnAllApiNames() {
        // arrange
        IdNamePairListDto mock = new IdNamePairListDto();
        mock.setResultList(List.of(new IdNamePairDto()));
        when(service.getAllApiKeyNames()).thenReturn(mock);

        // act
        IdNamePairListDto response = controller.getAllApiKeyNames();

        // assert
        assertNotNull(response);
        assertEquals(1, response.getResultList().size());
        verify(service).getAllApiKeyNames();
    }
}