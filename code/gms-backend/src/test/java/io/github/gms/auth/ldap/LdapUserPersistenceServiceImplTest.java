package io.github.gms.auth.ldap;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test of {@link LdapUserPersistenceServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class LdapUserPersistenceServiceImplTest extends AbstractUnitTest {
    
    private ListAppender<ILoggingEvent> logAppender;
    private Clock clock;
	private UserRepository repository;
    private LdapUserPersistenceServiceImpl service;

    @BeforeEach
	void beforeEach() {
		logAppender = new ListAppender<>();
		logAppender.start();

		clock = mock(Clock.class);
		repository = mock(UserRepository.class);
		service = new LdapUserPersistenceServiceImpl(clock, repository, false);
		((Logger) LoggerFactory.getLogger(LdapUserPersistenceServiceImpl.class)).addAppender(logAppender);
	}

    @AfterEach
	void tearDown() {
		logAppender.list.clear();
		logAppender.stop();
	}

    @Test
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
		service = new LdapUserPersistenceServiceImpl(clock, repository, true);

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
		service = new LdapUserPersistenceServiceImpl(clock, repository, storeLdapCredential);
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
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
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
	}
}