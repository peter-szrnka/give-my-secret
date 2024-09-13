package io.github.gms.functions.iprestriction;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.common.util.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import java.util.List;

import static io.github.gms.util.TestUtils.assertLogContains;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class IpRestrictionValidatorTest extends AbstractLoggingUnitTest {

    private HttpServletRequest httpServletRequest;
    private IpRestrictionValidator validator;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        httpServletRequest = mock(HttpServletRequest.class);
        validator = new IpRestrictionValidator(httpServletRequest);
        addAppender(IpRestrictionValidator.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"127.0.0.1", "0:0:0:0:0:0:0:1"})
    void shouldCheckIpRestrictionsBySecretWithoutRules(String ipAddress) {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // arrange
            List<IpRestrictionPattern> input = List.of();
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn(ipAddress);

            // act
            boolean response = validator.isIpAddressBlocked(input);

            // assert
            assertFalse(response);
            assertLogContains(logAppender, "Client IP address: " + ipAddress);
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    @ParameterizedTest
    @MethodSource("restrictionInputData")
    void shouldFailWhenIpIsRestricted(boolean allow, String ipPattern, String ipAddress) {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // arrange
            List<IpRestrictionPattern> input = List.of(IpRestrictionPattern.builder().ipPattern(ipPattern).allow(allow).build());
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn(ipAddress);

            // act
            boolean response = validator.isIpAddressBlocked(input);

            // assert
            assertTrue(response);
            assertLogContains(logAppender, "Client IP address: " + ipAddress);
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    @ParameterizedTest
    @MethodSource("positiveRestrictionInputData")
    void shouldNotFailWhenIpIsNotRestricted(boolean allow, String ipPattern, String ipAddress) {
        try (MockedStatic<HttpUtils> httpUtilsMockedStatic = mockStatic(HttpUtils.class)) {
            // arrange
            List<IpRestrictionPattern> input = List.of(IpRestrictionPattern.builder().ipPattern(ipPattern).allow(allow).build());
            httpUtilsMockedStatic.when(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)))
                    .thenReturn(ipAddress);

            // act
            boolean response = validator.isIpAddressBlocked(input);

            // assert
            assertFalse(response);
            assertLogContains(logAppender, "Client IP address: " + ipAddress);
            httpUtilsMockedStatic.verify(() -> HttpUtils.getClientIpAddress(eq(httpServletRequest)));
        }
    }

    private static Object[][] restrictionInputData() {
        return new Object[][]{
                {true, "(192.168.0.)[0-9]{1,3}", "127.0.0.1"},
                {false, "(192.168.0.)[0-9]{1,3}", "192.168.0.2"},
        };
    }

    private static Object[][] positiveRestrictionInputData() {
        return new Object[][]{
                {false, "(192.168.0.)[0-9]{1,3}", "127.0.0.1"},
                {true, "(192.168.0.)[0-9]{1,3}", "192.168.0.2"},
                {false, "(192.168.0.)[0-9]{1,3}", "0:0:0:0:0:0:0:1"}
        };
    }
}
