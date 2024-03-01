package io.github.gms.functions.event;

import io.github.gms.abstraction.AbstractSecurityTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.github.gms.util.TestConstants.TAG_SECURITY_TEST;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Tag(TAG_SECURITY_TEST)
class EventAdminRoleSecurityTest extends AbstractSecurityTest {

	public EventAdminRoleSecurityTest() {
		super("/event");
	}

	@Test
	void testListFailWithHttp403() {
		shouldListFailWith403(EventListDto.class);
	}
}