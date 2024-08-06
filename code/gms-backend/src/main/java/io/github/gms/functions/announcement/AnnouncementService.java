package io.github.gms.functions.announcement;

import io.github.gms.common.abstraction.GmsService;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.service.CountService;
import org.springframework.data.domain.Pageable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface AnnouncementService extends GmsService, CountService {
	
	SaveEntityResponseDto save(SaveAnnouncementDto dto);

	AnnouncementListDto list(Pageable pageable);
	
	AnnouncementDto getById(Long id);
}
