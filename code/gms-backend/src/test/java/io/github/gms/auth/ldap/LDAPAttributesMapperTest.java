package io.github.gms.auth.ldap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.util.Constants;
import lombok.SneakyThrows;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Disabled("Temporarily disabled")
class LDAPAttributesMapperTest extends AbstractUnitTest {

	private static final LDAPAttributesMapper mapper = new LDAPAttributesMapper();

	@SneakyThrows
	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void shouldMapAttributes(boolean returnRoles) {
		// arrange
		Attributes input = mock(Attributes.class);

		mockAttribute(input, Constants.LDAP_PROPERTY_CN, "user1", true);
		mockAttribute(input, Constants.LDAP_PROPERTY_UID, "My User", true);
		mockAttribute(input, Constants.LDAP_PROPERTY_CREDENTIAL, "Secret1!", true);
		mockAttribute(input, Constants.LDAP_PROPERTY_EMAIL, "my.email@email.com", false);
		mockAttributeCollection(input, returnRoles);

		// act
		GmsUserDetails response = mapper.mapFromAttributes(input);

		// assert
		assertNotNull(response);
		assertEquals(returnRoles, !response.getAuthorities().isEmpty());
	}

	private void mockAttributeCollection(Attributes attributes, boolean returnValue) throws NamingException {
		if (!returnValue) {
			when(attributes.get(Constants.LDAP_PROPERTY_ROLE)).thenReturn(null);
			return;
		}

		Attribute attribute = mock(Attribute.class);
		when(attribute.get(0)).thenReturn(UserRole.ROLE_ADMIN.name());
		when(attribute.get(1)).thenReturn(UserRole.ROLE_USER.name());
		when(attribute.size()).thenReturn(2);

		when(attributes.get(Constants.LDAP_PROPERTY_ROLE)).thenReturn(attribute);
	}

	private void mockAttribute(Attributes attributes, String attributeName, String value, boolean returnValue)
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
