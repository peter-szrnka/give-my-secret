package io.github.gms.secure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.gms.common.abstraction.AbstractController;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.types.Audited;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.dto.ChangePasswordRequestDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserDto;
import io.github.gms.secure.dto.UserListDto;
import io.github.gms.secure.service.UserService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/user")
@AuditTarget(EventTarget.USER)
public class UserController extends AbstractController<UserService> {
	
	@PostMapping
	@PreAuthorize(Constants.ROLE_ADMIN_OR_USER)
	@Audited(operation = EventOperation.SAVE)
	public @ResponseBody SaveEntityResponseDto save(@RequestBody SaveUserRequestDto dto) {
		return service.save(dto);
	}

	@GetMapping("/{id}")
	@PreAuthorize(Constants.ROLE_ADMIN_OR_USER)
	public @ResponseBody UserDto getById(@PathVariable("id") Long id) {
		return service.getById(id);
	}
	
	@PostMapping("/list")
	@PreAuthorize(Constants.ROLE_ADMIN)
	public @ResponseBody UserListDto list(@RequestBody PagingDto dto) {
		return service.list(dto);
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Audited(operation = EventOperation.DELETE)
	public ResponseEntity<String> delete(@PathVariable("id") Long id) {
		service.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Audited(operation = EventOperation.TOGGLE_STATUS)
	public ResponseEntity<String> toggle(@PathVariable("id") Long id, @RequestParam boolean enabled) {
		service.toggleStatus(id, enabled);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/count")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody LongValueDto userCount() {
		return service.count();
	}
	
	@PostMapping("/change_credential")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_VIEWER','ROLE_USER')")
	public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequestDto dto) {
		service.changePassword(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
