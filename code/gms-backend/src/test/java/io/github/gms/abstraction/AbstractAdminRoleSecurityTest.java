package io.github.gms.abstraction;

import org.junit.jupiter.api.BeforeEach;

import io.github.gms.common.util.DemoDataProviderService;
import io.github.gms.util.TestUtils;

/**
 * The goal of these tests to provide negative scenarios when an admin wants to 
 * manipulate user data.
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractAdminRoleSecurityTest extends AbstractSecurityTest {

	@Override
	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsUser(DemoDataProviderService.USER_2_ID, DemoDataProviderService.USERNAME2,
				"ROLE_ADMIN");
		jwt = jwtService.generateJwt(gmsUser);
	}
}
