package io.github.gms.abstraction;

import org.junit.jupiter.api.BeforeEach;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public abstract class AbstractSecurityTest extends AbstractIntegrationTest {

	@Override
	@BeforeEach
	public void setup() {
		gmsUser = null;
		jwt = null;
	}
}
