package io.github.gms.functions.message;

import io.github.gms.common.abstraction.AbstractController;
import io.github.gms.common.dto.IdListDto;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.util.ConverterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.ALL_ROLE;
import static io.github.gms.common.util.Constants.ID;
import static io.github.gms.common.util.Constants.PATH_LIST;
import static io.github.gms.common.util.Constants.PATH_VARIABLE_ID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/secure/message")
public class MessageController {

	private final MessageService service;

	@GetMapping(PATH_LIST)
	@PreAuthorize(ALL_ROLE)
	public MessageListDto list(
			@RequestParam("direction") String direction,
			@RequestParam("property") String property,
			@RequestParam("page") int page,
			@RequestParam("size") int size) {
		return service.list(ConverterUtils.createPageable(direction, property, page, size));
	}
	
	@GetMapping("/unread")
	@PreAuthorize(ALL_ROLE)
	public LongValueDto unreadMessagesCount() {
		return new LongValueDto(service.getUnreadMessagesCount());
	}

	@PutMapping("/mark_as_read")
	@PreAuthorize(ALL_ROLE)
	public ResponseEntity<String> markAsRead(@RequestBody MarkAsReadRequestDto dto) {
		service.markAsRead(dto);
		return new ResponseEntity<>("", HttpStatus.OK);
	}

	@PostMapping("/delete_all_by_ids")
	@PreAuthorize(ALL_ROLE)
	public ResponseEntity<Void> deleteAllByIds(@RequestBody IdListDto dto) {
		service.deleteAllByIds(dto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/toggle_read_by_ids")
	@PreAuthorize(ALL_ROLE)
	public ResponseEntity<Void> toggleReadByIds(@RequestBody IdListDto dto, @RequestParam("opened") boolean opened) {
		service.toggleReadByIds(dto, opened);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping(PATH_VARIABLE_ID)
	@PreAuthorize(ALL_ROLE)
	public ResponseEntity<Void> deleteById(@PathVariable(ID) Long id) {
		service.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
