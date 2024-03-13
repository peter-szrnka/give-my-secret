package io.github.gms.auth.ldap;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.functions.user.UserConverter;
import io.github.gms.functions.user.UserEntity;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link LdapSyncServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class LdapSyncServiceImplTest extends AbstractUnitTest {
    
    private ListAppender<ILoggingEvent> logAppender;
	private LdapTemplate ldapTemplate;
	private UserRepository repository;
	private UserConverter converter;
    private LdapSyncServiceImpl service;

    @BeforeEach
	void beforeEach() {
		logAppender = new ListAppender<>();
		logAppender.start();

		ldapTemplate = mock(LdapTemplate.class);
		repository = mock(UserRepository.class);
		converter = mock(UserConverter.class);
		service = new LdapSyncServiceImpl(ldapTemplate, repository, converter, "db");
		((Logger) LoggerFactory.getLogger(LdapSyncServiceImpl.class)).addAppender(logAppender);
	}

    @AfterEach
	void tearDown() {
		logAppender.list.clear();
		logAppender.stop();
	}

	@Test
	void shouldSkipLdapUserSync() {
		// arrange
		service = new LdapSyncServiceImpl(ldapTemplate, repository, converter, "db");

		// act
		int response = service.synchronizeUsers();

		// assert
		assertEquals(0, response);
		verify(ldapTemplate, never()).search(any(LdapQuery.class), any(AttributesMapper.class));
	}

	@ParameterizedTest
	@MethodSource("testData")
	void shouldSyncAllUsers(String username, boolean findUser) {
		// arrange
		service = new LdapSyncServiceImpl(ldapTemplate, repository, converter, "ldap");
		GmsUserDetails mockUser = TestUtils.createGmsUser();
		UserEntity mockEntity = TestUtils.createUser();
		mockUser.setUsername(username);
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of(mockUser));
		when(repository.findByUsername(username)).thenReturn(findUser ? Optional.of(mockEntity) : Optional.empty());
		if (findUser) {
			when(converter.toEntity(eq(mockUser), eq(mockEntity))).thenReturn(mockEntity);
		} else {
			when(converter.toEntity(eq(mockUser), isNull())).thenReturn(mockEntity);
		}
		when(repository.save(eq(mockEntity))).thenReturn(mockEntity);

		// act
		int response = service.synchronizeUsers();

		// assert
		assertEquals(1, response);
		verify(ldapTemplate).search(any(LdapQuery.class), any(AttributesMapper.class));
		verify(repository).findByUsername(username);
		if (findUser) {
			verify(converter).toEntity(eq(mockUser), eq(mockEntity));
		} else {
			verify(converter).toEntity(eq(mockUser), isNull());
		}
	}

	private static Object[][] testData() {
		return new Object[][] {
				{ "test1", true },
				{ "test2", false }
		};
	}

    /*@Test
	void shouldNotUpdateCredentials() {
		// arrange
		when(repository.findByUsername("test")).thenReturn(Optional.of(TestUtils.createUser()));

		// act
		UserDetails response = service.saveUserIfRequired("test", TestUtils.createGmsUser());

		// assert
		assertNotNull(response);		
	}
	
	@Test
	void shouldNotUpdateCredentialsWhenMatching() {
		// arrange
		service = new LdapSyncServiceImpl(ldapTemplate, repository, converter, "db");

		GmsUserDetails userDetails = TestUtils.createGmsUser();
		userDetails.setCredential("test-credential");

		UserEntity entity = TestUtils.createUser();
		entity.setCredential("test-credential");
		when(repository.findByUsername("test")).thenReturn(Optional.of(entity));

		// act
		UserDetails response = service.saveUserIfRequired("test", userDetails);


		// assert
		assertNotNull(response);
		verify(repository, never()).save(any(UserEntity.class));
	}
	
	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void shouldUpdateCredentials(boolean storeLdapCredential) {
		// arrange
		service = new LdapSyncServiceImpl(ldapTemplate, repository, converter, "db");
		UserEntity testMockUser = TestUtils.createUser();
		testMockUser.setId(3L);
		when(repository.findByUsername("test")).thenReturn(Optional.of(testMockUser));

		// act
		GmsUserDetails response = service.saveUserIfRequired("test", TestUtils.createGmsUser());

		// assert
		assertNotNull(response);
		assertEquals(3L, response.getUserId());

		if (storeLdapCredential) {
			ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
			verify(repository).save(userEntityCaptor.capture());
			UserEntity capturedUserEntity = userEntityCaptor.getValue();
			assertEquals("UserEntity(id=3, name=name, username=username, email=a@b.com, status=ACTIVE, credential=test, creationDate=null, roles=ROLE_USER, mfaEnabled=false, mfaSecret=null, failedAttempts=0)", capturedUserEntity.toString());
			TestUtils.assertLogContains(logAppender, "Credential has been updated for user=");
		}
	}
	
	@Test
	void shouldSaveNewLdapUser() {
		// arrange
		service = new LdapSyncServiceImpl(ldapTemplate, repository, converter, "ldap");
		when(repository.findByUsername("test")).thenReturn(Optional.empty());
		UserEntity testMockUser = TestUtils.createUser();
		testMockUser.setId(3L);
		when(repository.save(any(UserEntity.class))).thenReturn(testMockUser);

		// act
		GmsUserDetails userDetails = TestUtils.createGmsUser();
		userDetails.setMfaEnabled(true);
		GmsUserDetails response = service.saveUserIfRequired("test", userDetails);

		// assert
		assertNotNull(response);
		assertEquals(3L, response.getUserId());
		assertEquals("GmsUserDetails(name=username1, email=a@b.com, userId=3, username=username1, credential=test, authorities=[ROLE_USER], accountNonLocked=true, enabled=true, mfaEnabled=true, mfaSecret=MFA_SECRET)", response.toString());
		
		ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(repository).save(userEntityCaptor.capture());
		
		UserEntity capturedUserEntity = userEntityCaptor.getValue();
		assertEquals("UserEntity(id=null, name=username1, username=username1, email=a@b.com, status=ACTIVE, credential=*PROVIDED_BY_LDAP*, creationDate=2023-06-29T00:00Z, roles=ROLE_USER, mfaEnabled=true, mfaSecret=MFA_SECRET, failedAttempts=0)", capturedUserEntity.toString());
		TestUtils.assertLogContains(logAppender, "User data has been saved into DB for user=");
	}*/
}