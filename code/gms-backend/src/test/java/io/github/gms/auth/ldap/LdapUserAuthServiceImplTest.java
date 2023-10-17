package io.github.gms.auth.ldap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class LdapUserAuthServiceImplTest extends AbstractUnitTest {

	private LdapTemplate ldapTemplate;
	private LdapUserPersistenceService ldapUserPersistenceService;
	private LdapUserAuthServiceImpl service;

	@BeforeEach
	public void setup() {
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");
		ldapTemplate = mock(LdapTemplate.class);
		ldapUserPersistenceService = mock(LdapUserPersistenceService.class);
		service = new LdapUserAuthServiceImpl(ldapTemplate, ldapUserPersistenceService);
	}

	@Test
	@SuppressWarnings("unchecked")
	void shouldNotFoundUser() {
		// arrange
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of());
		
		// act
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("test"));
		
		// assert
		verify(ldapTemplate).search(any(LdapQuery.class), any(AttributesMapper.class));
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
		verify(ldapTemplate).search(any(LdapQuery.class), any(AttributesMapper.class));
		assertEquals("User not found!", exception.getMessage());
	}

	@Test
	@SuppressWarnings("unchecked")
	void shouldFoundOneUser() {
		// arrange
		GmsUserDetails mockUser = TestUtils.createGmsUser();
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of(mockUser));
		when(ldapUserPersistenceService.saveUserIfRequired(anyString(), eq(mockUser))).thenReturn(mockUser);

		// act
		UserDetails response = service.loadUserByUsername("test");

		// assert
		assertNotNull(response);
		assertEquals(DemoData.USERNAME1, response.getUsername());
		verify(ldapTemplate).search(any(LdapQuery.class), any(AttributesMapper.class));
		verify(ldapUserPersistenceService).saveUserIfRequired(anyString(), eq(mockUser));
	}
}
