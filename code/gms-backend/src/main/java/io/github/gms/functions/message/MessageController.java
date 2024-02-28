package io.github.gms.functions.message;

import io.github.gms.common.abstraction.AbstractController;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.dto.PagingDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.ALL_ROLE;
import static io.github.gms.common.util.Constants.PATH_LIST;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/message")
public class MessageController extends AbstractController<MessageService> {

	public MessageController(MessageService service) {
		super(service);
	}

	@PostMapping(PATH_LIST)
	@PreAuthorize(ALL_ROLE)
	public MessageListDto list(@RequestBody PagingDto dto) {
		return service.list(dto);
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
}
