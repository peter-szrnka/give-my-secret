package io.github.gms.functions.announcement;

import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.functions.user.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;

import static io.github.gms.common.util.Constants.ENTITY_NOT_FOUND;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

	private final Clock clock;
	private final AnnouncementRepository repository;
	private final UserService userService;

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
		Page<AnnouncementEntity> results = repository.findAll(ConverterUtils.createPageable(dto));
		return AnnouncementListDto.builder()
				.resultList(results.toList().stream()
						.map(this::toDto)
						.toList())
				.totalElements(results.getTotalElements())
				.build();
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

	@Override
	public LongValueDto count() {
		return new LongValueDto(repository.count());
	}
}
