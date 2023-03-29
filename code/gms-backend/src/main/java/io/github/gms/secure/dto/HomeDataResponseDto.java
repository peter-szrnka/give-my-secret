package io.github.gms.secure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDataResponseDto {

    private AnnouncementListDto announcements = new AnnouncementListDto();
    private EventListDto events = new EventListDto();
    private long announcementCount;
    private long apiKeyCount;
    private long keystoreCount;
    private long secretCount;
    private long userCount;
}
