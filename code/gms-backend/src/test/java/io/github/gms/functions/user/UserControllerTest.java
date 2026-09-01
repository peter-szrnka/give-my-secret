package io.github.gms.functions.user;

import dev.samstevens.totp.exceptions.QrGenerationException;
import io.github.gms.abstraction.AbstractClientControllerTest;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link UserController}
 * 
 * @author Peter Szrnka
 */
class UserControllerTest extends AbstractClientControllerTest<UserService, UserController> {

    @BeforeEach
    void setup() {
        service = mock(UserService.class);
        controller = new UserController(service);
    }

    @Test
    void delete_whenInputProvided_thenReturnOk() {
        // given
        doNothing().when(service).delete(1L);

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
        SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();
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
        UserDto dto = TestUtils.createUserDto();
        when(service.getById(1L)).thenReturn(dto);

        // when
        UserDto response = controller.getById(1L);

        // then
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getById(1L);
    }

    @Test
    void list_whenInputProvided_thenReturnOk() {
        // given
        UserListDto dtoList = TestUtils.createUserListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);

        // when
        UserListDto response = controller.list(
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
    void changePassword_whenInputProvided_thenReturnOk() {
        // given
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("old", "new");
        doNothing().when(service).changePassword(dto);

        // when
        ResponseEntity<Void> response = controller.changePassword(dto);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).changePassword(dto);
    }

    @Test
    void getMfaQrCode_whenExceptionOccurred_thenReturnHttp400() throws QrGenerationException {
        // given
        when(service.getMfaQrCode()).thenThrow(QrGenerationException.class);

        // when
        ResponseEntity<byte[]> response = controller.getMfaQrCode();

        // then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        verify(service).getMfaQrCode();
    }

    @Test
    void getMfaQrCode_whenInputProvided_thenReturnOk() throws QrGenerationException {
        // given
        when(service.getMfaQrCode()).thenReturn("QR-url".getBytes());

        // when
        ResponseEntity<byte[]> response = controller.getMfaQrCode();

        // then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("QR-url", new String(response.getBody()));
        verify(service).getMfaQrCode();
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void toggleMfa_whenInputProvided_thenReturnOk(boolean input) {
        // given
        doNothing().when(service).toggleMfa(input);

        // when
        ResponseEntity<Void> response = controller.toggleMfa(input);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).toggleMfa(input);
    }

    @Test
    void isMfaActive_whenInputProvided_thenReturnOk() {
        // given
        when(service.isMfaActive()).thenReturn(true);

        // when
        ResponseEntity<Boolean> response = controller.isMfaActive();

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, response.getBody());
        verify(service).isMfaActive();
    }
}
