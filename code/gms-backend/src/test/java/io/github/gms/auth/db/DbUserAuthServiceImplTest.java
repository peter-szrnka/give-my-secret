package io.github.gms.auth.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class DbUserAuthServiceImplTest extends AbstractUnitTest {

	@Mock
	private UserRepository repository;

	@InjectMocks
	private DbUserAuthServiceImpl service;
	
	@Test
	void shouldNotFoundUser() {
		when(repository.findByUsername(anyString())).thenReturn(Optional.empty());
		
		// act
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("test"));
		
		// assert
		assertEquals("User not found!", exception.getMessage());
	}
	
	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void shoulFoundUser(boolean isActive) {
		when(repository.findByUsername(anyString())).thenReturn(Optional.of(TestUtils.createUserWithStatus(isActive? EntityStatus.ACTIVE : EntityStatus.DISABLED)));
		
		// act
		UserDetails response = service.loadUserByUsername("test");
		
		// assert
		assertNotNull(response);
		assertEquals(isActive, response.isEnabled());
	}
}
