package io.github.gms.abstraction;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;

import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.util.DemoData;
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
		gmsUser = TestUtils.createGmsUser(DemoData.USER_2_ID, DemoData.USERNAME2,
				"ROLE_ADMIN");
		
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_ID.getDisplayName(), DemoData.USER_2_ID,
				MdcParameter.USER_NAME.getDisplayName(), DemoData.USERNAME2,
				"roles", List.of("ROLE_ADMIN")
		);
		
		
		jwt = jwtService.generateJwt(GenerateJwtRequest.builder()
				.subject(DemoData.USERNAME2)
				.algorithm("HS512")
				.expirationDateInSeconds(60L)
				.claims(claims)
				.build());
	}
}
