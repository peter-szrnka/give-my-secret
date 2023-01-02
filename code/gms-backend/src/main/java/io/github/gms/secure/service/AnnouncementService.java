package io.github.gms.secure.service;

import io.github.gms.common.abstraction.GmsService;
import io.github.gms.secure.dto.AnnouncementDto;
import io.github.gms.secure.dto.AnnouncementListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveAnnouncementDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface AnnouncementService extends GmsService {
	
	SaveEntityResponseDto save(SaveAnnouncementDto dto);

	AnnouncementListDto list(PagingDto dto);
	
	AnnouncementDto getById(Long id);
}
