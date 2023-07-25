package io.github.gms.secure.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import io.github.gms.secure.dto.ChangePasswordRequestDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserDto;
import io.github.gms.secure.dto.UserListDto;
import io.github.gms.secure.service.UserService;
import io.github.gms.util.TestUtils;
import lombok.SneakyThrows;

/**
 * Unit test of {@link UserController}
 * 
 * @author Peter Szrnka
 */
class UserControllerTest extends AbstractClientControllerTest<UserService, UserController> {

    @BeforeEach
    void setupTest() {
        service = Mockito.mock(UserService.class);
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
        PagingDto pagingDto = new PagingDto("DESC", "id", 0, 10);
        UserListDto dtoList = TestUtils.createUserListDto();
        when(service.list(pagingDto)).thenReturn(dtoList);
        

        // act
        UserListDto response = controller.list(pagingDto);

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pagingDto);
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
        when(service.getMfaQrCode()).thenThrow(UnsupportedEncodingException.class);

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
        assertEquals(200, response.getStatusCode().value());
        assertEquals("QR-url".getBytes(), response.getBody());
        verify(service).getMfaQrCode();
    }
}