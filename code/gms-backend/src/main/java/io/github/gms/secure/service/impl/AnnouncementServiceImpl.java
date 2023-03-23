package io.github.gms.secure.service.impl;

import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.secure.dto.AnnouncementDto;
import io.github.gms.secure.dto.AnnouncementListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveAnnouncementDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.entity.AnnouncementEntity;
import io.github.gms.secure.repository.AnnouncementRepository;
import io.github.gms.secure.service.AnnouncementService;
import io.github.gms.secure.service.UserService;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import static io.github.gms.common.util.Constants.ENTITY_NOT_FOUND;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

	private final Clock clock;
	private final AnnouncementRepository repository;
	private final UserService userService;

	public AnnouncementServiceImpl(Clock clock, AnnouncementRepository repository, UserService userService) {
		this.clock = clock;
		this.repository = repository;
		this.userService = userService;
	}

	@Override
	public SaveEntityResponseDto save(SaveAnnouncementDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		AnnouncementEntity entity = new AnnouncementEntity();

		if (dto.getId() != null) {
			entity.setId(dto.getId());
		} else {
			entity.setAnnouncementDate(ZonedDateTime.now(clock));
		}

		entity.setAuthorId(userId);
		entity.setDescription(dto.getDescription());
		entity.setTitle(dto.getTitle());

		entity = repository.save(entity);
		return new SaveEntityResponseDto(entity.getId());
	}

	@Override
	public AnnouncementListDto list(PagingDto dto) {
		return new AnnouncementListDto(
				repository.findAll(ConverterUtils.createPageable(dto))
				.getContent()
				.stream()
				.map(this::toDto)
				.collect(Collectors.toList())
		);
	}
	
	@Override
	public AnnouncementDto getById(Long id) {
		return toDto(repository.findById(id)
				.orElseThrow(() -> new GmsException(ENTITY_NOT_FOUND)));
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
