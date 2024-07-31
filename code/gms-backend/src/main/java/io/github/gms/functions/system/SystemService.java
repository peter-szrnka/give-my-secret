package io.github.gms.functions.system;

import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.util.Constants;
import io.github.gms.functions.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static io.github.gms.common.util.Constants.OK;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_DB;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class SystemService {

	private final UserRepository userRepository;
	private final Clock clock;
	private final String authType;
	// It will be set with setter injection
	private BuildProperties buildProperties;

	public SystemService(UserRepository userRepository, Clock clock, @Value("${config.auth.type}") String authType) {
		this.userRepository = userRepository;
		this.clock = clock;
		this.authType = authType;
	}

	public SystemStatusDto getSystemStatus() {
		SystemStatusDto.SystemStatusDtoBuilder builder = SystemStatusDto.builder();
		builder.authMode(authType);
		builder.version(getVersion());
		builder.built(getBuildTime().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)));
		builder.containerId(System.getenv("HOSTNAME"));

		if (!SELECTED_AUTH_DB.equals(authType)) {
			return builder.status(OK).build();
		}

		long result = userRepository.countExistingAdmins();
		return builder.status(result > 0 ? OK : "NEED_SETUP").build();
	}

	@Autowired(required = false)
	public void setBuildProperties(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	private String getVersion() {
		return buildProperties != null ? buildProperties.getVersion() : "DevRuntime";
	}

	private ZonedDateTime getBuildTime() {
		return buildProperties != null ? buildProperties.getTime().atZone(clock.getZone()) : ZonedDateTime.now(clock);
	}
}
