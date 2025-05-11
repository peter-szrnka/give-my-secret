package io.github.gms.auth.ldap;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.functions.user.UserConverter;
import io.github.gms.functions.user.UserRepository;
import io.github.gms.util.DemoData;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static io.github.gms.util.TestConstants.TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class LdapUserAuthServiceImplTest extends AbstractUnitTest {

	private LdapTemplate ldapTemplate;
	private UserRepository userRepository;
	private UserConverter userConverter;
	private LdapUserAuthServiceImpl service;

	@BeforeEach
	void setup() {
		MDC.put(MdcParameter.USER_ID.getDisplayName(), "1");
		ldapTemplate = mock(LdapTemplate.class);
		userRepository = mock(UserRepository.class);
		userConverter = mock(UserConverter.class);
		service = new LdapUserAuthServiceImpl(ldapTemplate, userRepository, userConverter);
	}

	@Test
	void loadUserByUsername_whenUserNotFound_thenThrowUsernameNotFoundException() {
		// arrange
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of());
		
		// act
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(TEST));
		
		// assert
		verify(ldapTemplate).search(any(LdapQuery.class), any(AttributesMapper.class));
		assertEquals("User not found!", exception.getMessage());
	}
	
	@Test
	void loadUserByUsername_whenMultipleUsersFound_thenThrowUsernameNotFoundException() {
		// arrange
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of(TestUtils.createGmsUser(), TestUtils.createGmsUser()));
		
		// act
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(TEST));
		
		// assert
		verify(ldapTemplate).search(any(LdapQuery.class), any(AttributesMapper.class));
		assertEquals("User not found!", exception.getMessage());
	}

	@Test
	void loadUserByUsername_whenOneUserFound_thenLoadUserDetails() {
		// arrange
		GmsUserDetails mockUser = TestUtils.createGmsUser();
		when(ldapTemplate.search(any(LdapQuery.class), any(AttributesMapper.class))).thenReturn(List.of(mockUser));
		when(userConverter.addIdToUserDetails(mockUser, DemoData.USER_1_ID)).thenReturn(mockUser);
		when(userRepository.getIdByUsername(TEST)).thenReturn(Optional.of(DemoData.USER_1_ID));

		// act
		UserDetails response = service.loadUserByUsername(TEST);

		// assert
		assertNotNull(response);
		assertEquals(DemoData.USERNAME1, response.getUsername());
		verify(ldapTemplate).search(any(LdapQuery.class), any(AttributesMapper.class));
		verify(userConverter).addIdToUserDetails(mockUser, DemoData.USER_1_ID);
	}
}
