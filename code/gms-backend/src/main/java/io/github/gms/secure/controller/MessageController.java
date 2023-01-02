package io.github.gms.secure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.gms.common.abstraction.AbstractController;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.MarkAsReadRequestDto;
import io.github.gms.secure.dto.MessageListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.service.MessageService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequestMapping("/secure/message")
public class MessageController extends AbstractController<MessageService> {

	@PostMapping("/list")
	@PreAuthorize(Constants.ALL_ROLE)
	public @ResponseBody MessageListDto list(@RequestBody PagingDto dto) {
		return service.list(dto);
	}
	
	@GetMapping("/unread")
	@PreAuthorize(Constants.ALL_ROLE)
	public @ResponseBody LongValueDto unreadMessagesCount() {
		return new LongValueDto(service.getUnreadMessagesCount());
	}

	@PutMapping("/mark_as_read")
	@PreAuthorize(Constants.ALL_ROLE)
	public ResponseEntity<String> markAsRead(@RequestBody MarkAsReadRequestDto dto) {
		service.markAsRead(dto);
		return new ResponseEntity<>("", HttpStatus.OK);
	}
}
