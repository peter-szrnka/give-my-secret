package io.github.gms.secure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDataResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 5142397705164311963L;
    @Builder.Default
    private AnnouncementListDto announcements = new AnnouncementListDto();
	@Builder.Default
    private EventListDto events = new EventListDto();
    private long announcementCount;
    private long apiKeyCount;
    private long keystoreCount;
    private long secretCount;
    private long userCount;
}
