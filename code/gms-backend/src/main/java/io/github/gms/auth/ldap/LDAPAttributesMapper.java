package io.github.gms.auth.ldap;

import static io.github.gms.common.util.Constants.LDAP_PROPERTY_CN;
import static io.github.gms.common.util.Constants.LDAP_PROPERTY_CREDENTIAL;
import static io.github.gms.common.util.Constants.LDAP_PROPERTY_EMAIL;
import static io.github.gms.common.util.Constants.LDAP_PROPERTY_ROLE;
import static io.github.gms.common.util.Constants.LDAP_PROPERTY_UID;
import static io.github.gms.common.util.Constants.LDAP_PROPERTY_MFA_ENABLED;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.UserRole;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class LDAPAttributesMapper implements AttributesMapper<GmsUserDetails> {

	@Override
	public GmsUserDetails mapFromAttributes(Attributes attributes) throws NamingException {
		return GmsUserDetails.builder()
				.name(getAttribute(attributes, LDAP_PROPERTY_CN))
				.username(getAttribute(attributes, LDAP_PROPERTY_UID))
				.credential(getAttribute(attributes, LDAP_PROPERTY_CREDENTIAL))
				.email(getAttribute(attributes, LDAP_PROPERTY_EMAIL))
				.authorities(getAttributeCollection(attributes))
				.mfaEnabled("true".equals(getAttribute(attributes, LDAP_PROPERTY_MFA_ENABLED)))
				.build();
	}
	
	private Set<UserRole> getAttributeCollection(Attributes attributes) throws NamingException {
		Attribute result = attributes.get(LDAP_PROPERTY_ROLE);

		if (result == null) {
			return Collections.emptySet();
		}
		
		Set<UserRole> roles = new HashSet<>();
		for (int i = 0; i < result.size(); i++) {
			roles.add(UserRole.valueOf(result.get(i).toString()));
		}

		return roles;
	}
	
	private String getAttribute(Attributes attributes, String attributeName) throws NamingException {
		Attribute result = attributes.get(attributeName);
		
		if (result == null) {
			return "";
		}
		
		return result.get().toString();
	}
}
