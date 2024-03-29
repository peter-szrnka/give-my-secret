package io.github.gms.functions.user;

import io.github.gms.auth.ldap.LdapSyncService;
import io.github.gms.common.abstraction.AbstractAdminController;
import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.ALL_ROLE;
import static io.github.gms.common.util.Constants.ID;
import static io.github.gms.common.util.Constants.PATH_ENABLED;
import static io.github.gms.common.util.Constants.PATH_LIST;
import static io.github.gms.common.util.Constants.PATH_VARIABLE_ID;
import static io.github.gms.common.util.Constants.ROLE_ADMIN;
import static io.github.gms.common.util.Constants.ROLE_ADMIN_OR_USER;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_LDAP;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/user")
@AuditTarget(EventTarget.USER)
public class UserController extends AbstractAdminController<UserService> {

	private final LdapSyncService ldapSyncService;
	private final String authType;

	public UserController(
			UserService service,
			@Autowired(required = false) LdapSyncService ldapSyncService,
			@Value("${config.auth.type}") String authType) {
		super(service);
		this.ldapSyncService = ldapSyncService;
		this.authType = authType;
	}
	
	@PostMapping
	@PreAuthorize(ROLE_ADMIN_OR_USER)
	@Audited(operation = EventOperation.SAVE)
	public SaveEntityResponseDto save(@RequestBody SaveUserRequestDto dto) {
		return service.save(dto);
	}

	@GetMapping(PATH_VARIABLE_ID)
	@PreAuthorize(ROLE_ADMIN_OR_USER)
	public UserDto getById(@PathVariable(ID) Long id) {
		return service.getById(id);
	}
	
	@PostMapping(PATH_LIST)
	@PreAuthorize(ROLE_ADMIN)
	public UserListDto list(@RequestBody PagingDto dto) {
		return service.list(dto);
	}
	
	@PostMapping("/change_credential")
	@PreAuthorize(ALL_ROLE)
	public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequestDto dto) {
		service.changePassword(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/mfa_qr_code", produces = MimeTypeUtils.IMAGE_PNG_VALUE)
	@PreAuthorize(ALL_ROLE)
	public ResponseEntity<byte[]> getMfaQrCode() {
		try {
			return new ResponseEntity<>(service.getMfaQrCode(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/toggle_mfa")
	@PreAuthorize(ALL_ROLE)
	@Audited(operation = EventOperation.TOGGLE_MFA)
	public ResponseEntity<Void> toggleMfa(@RequestParam(PATH_ENABLED) boolean enabled) {
		service.toggleMfa(enabled);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/mfa_active")
	@PreAuthorize(ALL_ROLE)
	public ResponseEntity<Boolean> isMfaActive() {
		return new ResponseEntity<>(service.isMfaActive(), HttpStatus.OK);
	}

	@GetMapping("/sync_ldap_users")
	@PreAuthorize(ROLE_ADMIN)
	@Audited(operation = EventOperation.SYNC_LDAP_USERS_MANUALLY)
	public ResponseEntity<Void> synchronizeUsers() {
		if (!SELECTED_AUTH_LDAP.equals(authType)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		ldapSyncService.synchronizeUsers();
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
