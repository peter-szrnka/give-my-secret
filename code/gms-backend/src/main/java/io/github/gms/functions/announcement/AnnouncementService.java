package io.github.gms.functions.announcement;

import io.github.gms.common.abstraction.GmsService;
import io.github.gms.common.dto.PagingDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.service.CountService;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface AnnouncementService extends GmsService, CountService {
	
	SaveEntityResponseDto save(SaveAnnouncementDto dto);

	AnnouncementListDto list(PagingDto dto);
	
	AnnouncementDto getById(Long id);
}
