package io.github.gms.auth.db;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class DbUserAuthServiceImplTest extends AbstractUnitTest {

	private UserRepository repository;
	private DbUserAuthServiceImpl service;

	@BeforeEach
	void beforeEach() {
		repository = mock(UserRepository.class);
		service = new DbUserAuthServiceImpl(repository);
	}
	
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
		GmsUserDetails response = (GmsUserDetails) service.loadUserByUsername("test");
		
		// assert
		assertNotNull(response);
		assertEquals(1L, response.getUserId());
		assertEquals(TestUtils.USERNAME, response.getUsername());
		assertEquals(TestUtils.NEW_CREDENTIAL, response.getCredential());
		assertEquals("test@email.hu", response.getEmail());
		assertEquals(1, response.getAuthorities().size());
		assertEquals("name", response.getName());
		assertEquals(isActive, response.isAccountNonLocked());
		assertEquals(isActive, response.isEnabled());
	}
}
