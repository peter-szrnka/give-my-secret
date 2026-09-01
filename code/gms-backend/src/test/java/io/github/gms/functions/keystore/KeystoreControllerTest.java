package io.github.gms.functions.keystore;

import io.github.gms.abstraction.AbstractClientControllerTest;
import io.github.gms.common.dto.IdNamePairDto;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.functions.secret.dto.GetSecureValueDto;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static io.github.gms.util.TestConstants.TEST;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link KeystoreController}
 * 
 * @author Peter Szrnka
 */
class KeystoreControllerTest extends AbstractClientControllerTest<KeystoreService, KeystoreController> {

    @BeforeEach
    void setupTest() {
        service = mock(KeystoreService.class);
        controller = new KeystoreController(service);
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
        MultipartFile multiPartFile = new MockMultipartFile(TEST, "data".getBytes());
        when(service.save("{'test':'value'}", multiPartFile)).thenReturn(new SaveEntityResponseDto(2L));

        // when
        SaveEntityResponseDto response = controller.save("{'test':'value'}", multiPartFile);

        // then
        assertNotNull(response);
        assertEquals(2L, response.getEntityId());

        ArgumentCaptor<String> modelCaptor = ArgumentCaptor.forClass(String.class);
        verify(service).save(modelCaptor.capture(), eq(multiPartFile));
        assertEquals("{'test':'value'}", modelCaptor.getValue());
    }

    @Test
    void getById_whenInputProvided_thenReturnOk() {
        // given
        KeystoreDto dto = TestUtils.createKeystoreDto();
        when(service.getById(1L)).thenReturn(dto);

        // when
        KeystoreDto response = controller.getById(1L);

        // then
        assertNotNull(response);
        assertEquals(dto, response);
        verify(service).getById(1L);
    }

    @Test
    void list_whenInputProvided_thenReturnOk() {
        // given
        KeystoreListDto dtoList = TestUtils.createKeystoreListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);

        // when
        KeystoreListDto response = controller.list(
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
        GetSecureValueDto dto = new GetSecureValueDto();
        when(service.getValue(dto)).thenReturn(TEST);

        // when
        String response = controller.getValue(dto);

        // then
        assertNotNull(response);
        assertEquals(TEST, response);
        verify(service).getValue(dto);
    }

    @Test
    void getAllKeystoreNames_whenInputProvided_thenReturnOk() {
        // given
        IdNamePairListDto mock = new IdNamePairListDto();
        mock.setResultList(List.of(new IdNamePairDto()));
        when(service.getAllKeystoreNames()).thenReturn(mock);

        // when
        IdNamePairListDto response = controller.getAllKeystoreNames();

        // then
        assertNotNull(response);
        assertEquals(1, response.getResultList().size());
        verify(service).getAllKeystoreNames();
    }

    @Test
    void getAllKeystoreAliases_whenInputProvided_thenReturnOk() {
        // given
        IdNamePairListDto mock = new IdNamePairListDto();
        mock.setResultList(List.of(new IdNamePairDto()));
        when(service.getAllKeystoreAliasNames(1L)).thenReturn(mock);

        // when
        IdNamePairListDto response = controller.getAllKeystoreAliases(1L);

        // then
        assertNotNull(response);
        assertEquals(1, response.getResultList().size());
        verify(service).getAllKeystoreAliasNames(1L);
    }

    @Test
    void download_whenInputProvided_thenReturnOk() {
        // given
        DownloadFileResponseDto responseDto = new DownloadFileResponseDto("test.jks", "data".getBytes());
        when(service.downloadKeystore(1L)).thenReturn(responseDto);

        // when
        ResponseEntity<Resource> response = controller.download(1L);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("attachment; filename=\"test.jks\"", requireNonNull(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION)).getFirst());

        ByteArrayResource resource = (ByteArrayResource) response.getBody();
        assertNotNull(resource);
        assertEquals(4, resource.contentLength());
        assertEquals("data", new String(resource.getByteArray()));
    }
}
