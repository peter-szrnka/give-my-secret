package io.github.gms.functions.user;

import dev.samstevens.totp.exceptions.QrGenerationException;
import io.github.gms.abstraction.AbstractClientControllerTest;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link UserController}
 * 
 * @author Peter Szrnka
 */
class UserControllerTest extends AbstractClientControllerTest<UserService, UserController> {

    @BeforeEach
    void setupTest() {
        service = mock(UserService.class);
        controller = new UserController(service);
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
        SaveUserRequestDto dto = TestUtils.createSaveUserRequestDto();
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
        UserDto dto = TestUtils.createUserDto();
        when(service.getById(1L)).thenReturn(dto);

        // act
        UserDto response = controller.getById(1L);

        // assert
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getById(1L);
    }

    @Test
    void shouldReturnList() {
        // arrange
        UserListDto dtoList = TestUtils.createUserListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);

        // act
        UserListDto response = controller.list(
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
    void shouldChangePassword() {
        // arrange
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("old", "new");
        doNothing().when(service).changePassword(dto);

        // act
        ResponseEntity<Void> response = controller.changePassword(dto);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).changePassword(dto);
    }

    @Test
    @SneakyThrows
    void shouldNotReturnQrCode() {
        // arrange
        when(service.getMfaQrCode()).thenThrow(QrGenerationException.class);

        // act
        ResponseEntity<byte[]> response = controller.getMfaQrCode();

        // assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        verify(service).getMfaQrCode();
    }

    @Test
    @SneakyThrows
    void shouldReturnQrCode() {
        // arrange
        when(service.getMfaQrCode()).thenReturn("QR-url".getBytes());

        // act
        ResponseEntity<byte[]> response = controller.getMfaQrCode();

        // assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("QR-url", new String(response.getBody()));
        verify(service).getMfaQrCode();
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void shouldToggleMfa(boolean input) {
        // arrange
        doNothing().when(service).toggleMfa(input);

        // act
        ResponseEntity<Void> response = controller.toggleMfa(input);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(service).toggleMfa(input);
    }

    @Test
    @SneakyThrows
    void shouldReturnMfaIsActive() {
        // arrange
        when(service.isMfaActive()).thenReturn(true);

        // act
        ResponseEntity<Boolean> response = controller.isMfaActive();

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, response.getBody());
        verify(service).isMfaActive();
    }
}