package io.github.gms.auth.ldap;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.UserRole;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import static io.github.gms.common.util.Constants.LDAP_PROPERTY_CN;
import static io.github.gms.common.util.Constants.LDAP_PROPERTY_CREDENTIAL;
import static io.github.gms.common.util.Constants.LDAP_PROPERTY_EMAIL;
import static io.github.gms.common.util.Constants.LDAP_PROPERTY_MFA_ENABLED;
import static io.github.gms.common.util.Constants.LDAP_PROPERTY_ROLE;
import static io.github.gms.common.util.Constants.LDAP_PROPERTY_STATUS;
import static io.github.gms.common.util.Constants.LDAP_PROPERTY_UID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class LDAPAttributesMapperTest extends AbstractUnitTest {

	private static final LDAPAttributesMapper mapper = new LDAPAttributesMapper();

	@SneakyThrows
	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void mapFromAttributes_whenAllDataProvided_thenMapToUserDetails(boolean returnRoles) {
		// arrange
		Attributes input = mock(Attributes.class);

		mockAttribute(input, LDAP_PROPERTY_CN, "user1", true);
		mockAttribute(input, LDAP_PROPERTY_UID, "My User", true);
		mockAttribute(input, LDAP_PROPERTY_CREDENTIAL, "Secret1!", true);
		mockAttribute(input, LDAP_PROPERTY_EMAIL, "my.email@email.com", false);
		mockAttribute(input, LDAP_PROPERTY_MFA_ENABLED, "false", true);
		mockAttribute(input, LDAP_PROPERTY_STATUS, "ACTIVE", true);
		mockAttributeCollection(input, returnRoles);

		// act
		GmsUserDetails response = mapper.mapFromAttributes(input);

		// assert
		assertNotNull(response);
		assertEquals(returnRoles, !response.getAuthorities().isEmpty());
		assertEquals("user1", response.getName());
		assertEquals("My User", response.getUsername());
		assertEquals("Secret1!", response.getCredential());
		assertEquals(EntityStatus.ACTIVE, response.getStatus());
		assertFalse(response.isMfaEnabled());
		assertTrue(response.getAccountNonLocked());
		assertTrue(response.getEmail().isEmpty());
	}

	private static void mockAttributeCollection(Attributes attributes, boolean returnValue) throws NamingException {
		if (!returnValue) {
			when(attributes.get(LDAP_PROPERTY_ROLE)).thenReturn(null);
			return;
		}

		Attribute attribute = mock(Attribute.class);
		when(attribute.get(0)).thenReturn(UserRole.ROLE_ADMIN.name());
		when(attribute.get(1)).thenReturn(UserRole.ROLE_USER.name());
		when(attribute.size()).thenReturn(2);

		when(attributes.get(LDAP_PROPERTY_ROLE)).thenReturn(attribute);
	}

	private static void mockAttribute(Attributes attributes, String attributeName, String value, boolean returnValue)
			throws NamingException {
		if (!returnValue) {
			when(attributes.get(attributeName)).thenReturn(null);
			return;
		}

		Attribute attribute = mock(Attribute.class);
		when(attribute.get()).thenReturn(value);
		when(attributes.get(attributeName)).thenReturn(attribute);
	}
}
