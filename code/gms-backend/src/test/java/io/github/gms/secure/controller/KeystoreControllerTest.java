package io.github.gms.secure.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import io.github.gms.secure.dto.DownloadFileResponseDto;
import io.github.gms.secure.dto.GetSecureValueDto;
import io.github.gms.secure.dto.IdNamePairDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.service.KeystoreService;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link KeystoreController}
 * 
 * @author Peter Szrnka
 */
class KeystoreControllerTest extends AbstractClientControllerTest<KeystoreService, KeystoreController> {

    @BeforeEach
    void setupTest() {
        service = Mockito.mock(KeystoreService.class);
        controller = new KeystoreController(service);
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
        MultipartFile multiPartFile = new MockMultipartFile("test", "data".getBytes());
        when(service.save("{'test':'value'}", multiPartFile)).thenReturn(new SaveEntityResponseDto(2L));

        // act
        SaveEntityResponseDto response = controller.save("{'test':'value'}", multiPartFile);

        // assert
        assertNotNull(response);
        assertEquals(2L, response.getEntityId());

        ArgumentCaptor<String> modelCaptor = ArgumentCaptor.forClass(String.class);
        verify(service).save(modelCaptor.capture(), eq(multiPartFile));
        assertEquals("{'test':'value'}", modelCaptor.getValue());
    }

    @Test
    void shouldReturnById() {
        // arrange
        KeystoreDto dto = TestUtils.createKeystoreDto();
        when(service.getById(1L)).thenReturn(dto);

        // act
        KeystoreDto response = controller.getById(1L);

        // assert
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getById(1L);
    }

    @Test
    void shouldReturnList() {
        // arrange
        PagingDto pagingDto = new PagingDto("DESC", "id", 0, 10);
        KeystoreListDto dtoList = TestUtils.createKeystoreListDto();
        when(service.list(pagingDto)).thenReturn(dtoList);
        

        // act
        KeystoreListDto response = controller.list(pagingDto);

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pagingDto);
    }

    @Test
    void shouldReturnValue() {
        // arrange
        GetSecureValueDto dto = new GetSecureValueDto();
        when(service.getValue(dto)).thenReturn("test");

        // act
        String response = controller.getValue(dto);

        // assert
        assertNotNull(response);
        assertEquals("test", response);
        verify(service).getValue(dto);
    }

    @Test
    void shouldReturnAllKeystoreNames() {
        // arrange
        IdNamePairListDto mock = new IdNamePairListDto();
        mock.setResultList(List.of(new IdNamePairDto()));
        when(service.getAllKeystoreNames()).thenReturn(mock);

        // act
        IdNamePairListDto response = controller.getAllKeystoreNames();

        // assert
        assertNotNull(response);
        assertEquals(1, response.getResultList().size());
        verify(service).getAllKeystoreNames();
    }

    @Test
    void shouldReturnAllKeystoreAliases() {
        // arrange
        IdNamePairListDto mock = new IdNamePairListDto();
        mock.setResultList(List.of(new IdNamePairDto()));
        when(service.getAllKeystoreAliasNames(1L)).thenReturn(mock);

        // act
        IdNamePairListDto response = controller.getAllKeystoreAliases(1L);

        // assert
        assertNotNull(response);
        assertEquals(1, response.getResultList().size());
        verify(service).getAllKeystoreAliasNames(1L);
    }

    @Test
    void shouldDownload() {
        // arrange
        DownloadFileResponseDto responseDto = new DownloadFileResponseDto("test.jks", "data".getBytes());
        when(service.downloadKeystore(1L)).thenReturn(responseDto);

        // act
        ResponseEntity<Resource> response = controller.download(1L);

        // assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("attachment; filename=\"test.jks\"", response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0));

        ByteArrayResource resource = (ByteArrayResource) response.getBody();
        assertEquals(4, resource.contentLength());
        assertEquals("data", new String(resource.getByteArray()));
    }
}