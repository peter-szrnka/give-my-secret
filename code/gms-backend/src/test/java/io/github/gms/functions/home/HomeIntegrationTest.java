package io.github.gms.functions.home;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_INTEGRATION_TEST)
class HomeIntegrationTest extends AbstractIntegrationTest {

    @Test
    void getHomeData_whenUserIsAdmin_thenReturnData() {
        // arrange
        gmsUser = TestUtils.createGmsAdminUser();
        jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest(gmsUser));
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<HomeDataResponseDto> response = executeHttpGet("/secure/home/", requestEntity, HomeDataResponseDto.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getHomeData_whenUserIsUser_thenReturnData() {
        // arrange
        gmsUser = TestUtils.createGmsUser();
        jwt = jwtService.generateJwt(TestUtils.createJwtUserRequest());
        HttpEntity<Void> requestEntity = new HttpEntity<>(TestUtils.getHttpHeaders(jwt));

        // act
        ResponseEntity<HomeDataResponseDto> response = executeHttpGet("/secure/home/", requestEntity, HomeDataResponseDto.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
