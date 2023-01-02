package io.github.gms;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.gms.common.util.Constants;
import io.github.gms.util.TestConstants;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"dev", Constants.CONFIG_AUTH_TYPE_DB})
@Tag(TestConstants.TAG_INTEGRATION_TEST)
class DevProfileTest {
	
	@Autowired
	private ApplicationContext context;

	@Test
	void test() {
		assertNotNull(context);
	}
}
