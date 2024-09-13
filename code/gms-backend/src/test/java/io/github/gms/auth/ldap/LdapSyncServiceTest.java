package io.github.gms.auth.ldap;

import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.util.Pair;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link LdapSyncService}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class LdapSyncServiceTest extends AbstractLoggingUnitTest {

	private LdapTemplate ldapTemplate;
	private UserRepository repository;
	private LdapUserConverter converter;
    private LdapSyncService service;

	@Override
    @BeforeEach
	public void setup() {
		super.setup();

		ldapTemplate = mock(LdapTemplate.class);
		repository = mock(UserRepository.class);
		converter = mock(LdapUserConverter.class);
		service = new LdapSyncService(ldapTemplate, repository, converter);
		service.setAuthType("db");
		addAppender(LdapSyncService.class);
	}

	@Test
	void shouldSkipLdapUserSync() {
		// arrange
		service.setAuthType("db");

		// act
		Pair<Integer, Integer> response = service.synchronizeUsers();

		// assert
		assertNotNull(response);
		assertEquals(0, response.getFirst());
		assertEquals(0, response.getSecond());
		verify(ldapTemplate, never()).search(any(LdapQuery.class), any(AttributesMapper.class));
	}

	@ParameterizedTest
	@MethodSource("testData")
	void shouldSyncAllUsers(String username, boolean findUser) {
		// arrange
		service.setAuthType("ldap");
		GmsUserDetails mockUser = TestUtils.createGmsUser();
		UserEntity mockEntity = TestUtils.createUser();
		mockUser.setUsername(username);

		UserEntity nonExistingUser = TestUtils.createUser();
		nonExistingUser.setUsername("nonExistingUser");
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of(mockUser));
		when(repository.findByUsername(username)).thenReturn(findUser ? Optional.of(mockEntity) : Optional.of(nonExistingUser));
		if (findUser) {
			when(converter.toEntity(mockUser, mockEntity)).thenReturn(mockEntity);
		} else {
			when(converter.toEntity(any(GmsUserDetails.class), any(UserEntity.class))).thenReturn(mockEntity);
		}
		when(repository.save(mockEntity)).thenReturn(mockEntity);
		when(repository.getAllUserNames()).thenReturn(List.of(mockUser.getUsername(), nonExistingUser.getUsername()));

		// act
		Pair<Integer, Integer> response = service.synchronizeUsers();

		// assert
		assertNotNull(response);
		assertEquals(1, response.getFirst());
		assertEquals(1, response.getSecond());
		verify(ldapTemplate).search(any(LdapQuery.class), any(AttributesMapper.class));
		verify(repository).findByUsername(username);
		if (findUser) {
			verify(converter).toEntity(mockUser, mockEntity);
		} else {
			verify(converter).toEntity(any(GmsUserDetails.class), any(UserEntity.class));
		}
		verify(repository).markUserAsDeleted("nonExistingUser");
	}

	@Test
	void shouldNotSyncAllUsers() {
		// arrange
		service.setAuthType("ldap");
		GmsUserDetails mockUser = TestUtils.createGmsUser();
		UserEntity mockEntity = TestUtils.createUser();

		UserEntity nonExistingUser = TestUtils.createUser();
		nonExistingUser.setUsername("nonExistingUser");
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of(mockUser));
		when(repository.findByUsername("username1")).thenReturn(Optional.empty());
		when(converter.toEntity(any(GmsUserDetails.class), isNull())).thenReturn(mockEntity);
		when(repository.save(mockEntity)).thenReturn(mockEntity);
		when(repository.getAllUserNames()).thenReturn(List.of());

		// act
		Pair<Integer, Integer> response = service.synchronizeUsers();

		// assert
		assertNotNull(response);
		assertEquals(1, response.getFirst());
		verify(ldapTemplate).search(any(LdapQuery.class), any(AttributesMapper.class));
		verify(repository).findByUsername("username1");
		verify(repository).save(mockEntity);
		verify(converter).toEntity(any(GmsUserDetails.class), isNull());
	}

	private static Object[][] testData() {
		return new Object[][] {
				{ "test1", true },
				{ "test2", false }
		};
	}
}