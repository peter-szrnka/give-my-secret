package io.github.gms;

import static io.github.gms.common.util.Constants.CONFIG_AUTH_TYPE_DB;
import static io.github.gms.util.TestConstants.TAG_INTEGRATION_TEST;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"dev", CONFIG_AUTH_TYPE_DB})
@Tag(TAG_INTEGRATION_TEST)
class DevProfileTest {
	
	@Autowired
	private ApplicationContext context;

	@Test
	void test() {
		assertNotNull(context);
	}
}
