package io.github.gms.functions.iprestriction;

import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link IpRestrictionController}
 *
 * @author Peter Szrnka
 * @since 1.0
 */
class IpRestrictionControllerTest {

    private IpRestrictionController controller;
    private IpRestrictionService service;

    @BeforeEach
    void setup() {
        service = Mockito.mock(IpRestrictionService.class);
        controller = new IpRestrictionController(service);
    }

    @Test
    void save_whenInputProvided_thenReturnOk() {
        // arrange
        IpRestrictionDto dto = TestUtils.createIpRestrictionDto();
        when(service.save(dto)).thenReturn(new SaveEntityResponseDto(1L));

        // act
        SaveEntityResponseDto response = controller.save(dto);

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getEntityId());
        verify(service).save(dto);
    }

    @Test
    void getById_whenInputProvided_thenReturnOk() {
        // arrange
        IpRestrictionDto dto = TestUtils.createIpRestrictionDto();
        when(service.getById(1L)).thenReturn(dto);

        // act
        IpRestrictionDto response = controller.getById(1L);

        // assert
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getById(1L);
    }

    @Test
    void list_whenInputProvided_thenReturnOk() {
        // arrange
        IpRestrictionListDto dtoList = TestUtils.createIpRestrictionListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);


        // act
        IpRestrictionListDto response = controller.list(
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

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void toggle_whenInputProvided_thenReturnOk(boolean status) {
        // arrange
        doNothing().when(service).toggleStatus(1L, status);

        // act
        ResponseEntity<String> response = controller.toggle(1L, status);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).toggleStatus(1L, status);
    }
}
