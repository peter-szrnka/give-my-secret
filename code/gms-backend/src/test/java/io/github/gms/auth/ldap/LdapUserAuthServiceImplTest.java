package io.github.gms.auth.ldap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class LdapUserAuthServiceImplTest extends AbstractUnitTest {

	private ListAppender<ILoggingEvent> logAppender;
	private Clock clock;
	private UserRepository repository;
	private LdapTemplate ldapTemplate;
	private LdapUserAuthServiceImpl service;

	@BeforeEach
	public void setup() {
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");
		
		logAppender = new ListAppender<>();
		logAppender.start();
	}

	@BeforeEach
	void beforeEach() {
		logAppender = new ListAppender<>();
		logAppender.start();

		clock = mock(Clock.class);
		repository = mock(UserRepository.class);
		ldapTemplate = mock(LdapTemplate.class);
		service = new LdapUserAuthServiceImpl(clock, repository, ldapTemplate, false);
		((Logger) LoggerFactory.getLogger(LdapUserAuthServiceImpl.class)).addAppender(logAppender);
	}
	
	@AfterEach
	public void teardown() {
		logAppender.list.clear();
		logAppender.stop();
	}

	@Test
	@SuppressWarnings("unchecked")
	void shouldNotFoundUser() {
		// arrange
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of());
		
		// act
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("test"));
		
		// assert
		assertEquals("User not found!", exception.getMessage());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void shouldFoundMoreUser() {
		// arrange
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of(TestUtils.createGmsUser(), TestUtils.createGmsUser()));
		
		// act
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("test"));
		
		// assert
		assertEquals("User not found!", exception.getMessage());
	}

	@Test
	@SuppressWarnings("unchecked")
	void shouldNotUpdateCredentials() {
		// arrange
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of(TestUtils.createGmsUser()));
		when(repository.findByUsername("test")).thenReturn(Optional.of(TestUtils.createUser()));

		// act
		UserDetails response = service.loadUserByUsername("test");

		// assert
		assertNotNull(response);		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void shouldNotUpdateCredentialsWhenMatching() {
		// arrange
		service = new LdapUserAuthServiceImpl(clock, repository, ldapTemplate, true);

		GmsUserDetails userDetails = TestUtils.createGmsUser();
		userDetails.setCredential("test-credential");
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of(userDetails));
		
		UserEntity entity = TestUtils.createUser();
		entity.setCredential("test-credential");
		when(repository.findByUsername("test")).thenReturn(Optional.of(entity));

		// act
		UserDetails response = service.loadUserByUsername("test");

		// assert
		assertNotNull(response);
		verify(repository, never()).save(any(UserEntity.class));
	}
	
	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	@SuppressWarnings("unchecked")
	void shouldUpdateCredentials(boolean storeLdapCredential) {
		// arrange
		service = new LdapUserAuthServiceImpl(clock, repository, ldapTemplate, storeLdapCredential);

		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of(TestUtils.createGmsUser()));
		when(repository.findByUsername("test")).thenReturn(Optional.of(TestUtils.createUser()));

		// act
		UserDetails response = service.loadUserByUsername("test");

		// assert
		assertNotNull(response);

		if (storeLdapCredential) {
			ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
			verify(repository).save(userEntityCaptor.capture());
			UserEntity capturedUserEntity = userEntityCaptor.getValue();
			assertEquals("UserEntity(id=1, name=name, username=username, email=a@b.com, status=ACTIVE, credential=test, creationDate=null, roles=ROLE_USER)", capturedUserEntity.toString());
			TestUtils.assertLogContains(logAppender, "Credential has been updated for user=");
		}
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void shouldSaveNewLdapUser() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		service = new LdapUserAuthServiceImpl(clock, repository, ldapTemplate, false);
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of(TestUtils.createGmsUser()));
		when(repository.findByUsername("test")).thenReturn(Optional.empty());
		when(repository.save(any(UserEntity.class))).thenReturn(TestUtils.createUser());

		// act
		UserDetails response = service.loadUserByUsername("test");

		// assert
		assertNotNull(response);
		assertEquals("GmsUserDetails(name=username1, email=a@b.com, userId=1, username=username1, credential=test, authorities=[ROLE_USER], accountNonLocked=true, enabled=true)", response.toString());
		
		ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
		verify(repository).save(userEntityCaptor.capture());
		
		UserEntity capturedUserEntity = userEntityCaptor.getValue();
		assertEquals("UserEntity(id=null, name=username1, username=username1, email=a@b.com, status=ACTIVE, credential=*PROVIDED_BY_LDAP*, creationDate=2023-06-29T00:00Z, roles=ROLE_USER)", capturedUserEntity.toString());
		TestUtils.assertLogContains(logAppender, "User data has been saved into DB for user=");
	}
}
