package io.github.gms.abstraction;

import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.Map;

/**
 * The goal of these tests to provide negative scenarios when a user wants to
 * manipulate admin data.
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractUserRoleSecurityTest extends AbstractSecurityTest {

	@Override
	@BeforeEach
	public void setup() {
		gmsUser = TestUtils.createGmsUser(DemoData.USER_1_ID, DemoData.USERNAME1,
				"ROLE_USER");
		
		Map<String, Object> claims = Map.of(
				MdcParameter.USER_ID.getDisplayName(), DemoData.USER_1_ID,
				MdcParameter.USER_NAME.getDisplayName(), DemoData.USERNAME1,
				"roles", List.of("ROLE_USER")
		);
		
		
		jwt = jwtService.generateJwt(GenerateJwtRequest.builder()
				.subject(DemoData.USERNAME1)
				.algorithm("HS512")
				.expirationDateInSeconds(60L)
				.claims(claims)
				.build());
	}
}
