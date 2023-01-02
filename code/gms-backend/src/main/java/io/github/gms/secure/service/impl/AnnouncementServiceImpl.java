package io.github.gms.secure.service.impl;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.github.gms.common.entity.AnnouncementEntity;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.dto.AnnouncementDto;
import io.github.gms.secure.dto.AnnouncementListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveAnnouncementDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.repository.AnnouncementRepository;
import io.github.gms.secure.service.AnnouncementService;
import io.github.gms.secure.service.UserService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {
	
	@Autowired
	private Clock clock;
	
	@Autowired
	private AnnouncementRepository repository;
	
	@Autowired
	private UserService userService;

	@Override
	public SaveEntityResponseDto save(SaveAnnouncementDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		AnnouncementEntity entity = new AnnouncementEntity();

		if (dto.getId() != null) {
			entity.setId(dto.getId());
		} else {
			entity.setAnnouncementDate(LocalDateTime.now(clock));
		}

		entity.setAuthorId(userId);
		entity.setDescription(dto.getDescription());
		entity.setTitle(dto.getTitle());

		entity = repository.save(entity);
		return new SaveEntityResponseDto(entity.getId());
	}

	@Override
	public AnnouncementListDto list(PagingDto dto) {
		Sort sort = Sort.by(Direction.valueOf(dto.getDirection()), dto.getProperty());
		Pageable pagingRequest = PageRequest.of(dto.getPage(), dto.getSize(), sort);
		
		return new AnnouncementListDto(
				repository.findAll(pagingRequest)
				.getContent()
				.stream()
				.map(this::toDto)
				.collect(Collectors.toList())
		);
	}
	
	@Override
	public AnnouncementDto getById(Long id) {
		return toDto(repository.findById(id)
				.orElseThrow(() -> new GmsException(Constants.ENTITY_NOT_FOUND)));
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}
	
	private AnnouncementDto toDto(AnnouncementEntity announcement) {
		return AnnouncementDto.builder()
		.announcementDate(announcement.getAnnouncementDate())
		.author(userService.getUsernameById(announcement.getAuthorId()))
		.description(announcement.getDescription())
		.id(announcement.getId())
		.title(announcement.getTitle())
		.build();
	}
}
