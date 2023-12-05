package io.github.gms.secure.service.impl;

import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.event.RefreshCacheEvent;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static io.github.gms.common.util.Constants.OK;
import static io.github.gms.common.util.Constants.SELECTED_AUTH;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_DB;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_LDAP;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class SystemServiceImpl implements SystemService {

	private final UserRepository userRepository;
	private final Clock clock;
	private final Environment env;
	// It will be set with setter injection
	private BuildProperties buildProperties;

	public SystemServiceImpl(UserRepository userRepository, Clock clock, Environment env) {
		this.userRepository = userRepository;
		this.clock = clock;
		this.env = env;
	}

	@Override
	public SystemStatusDto getSystemStatus() {
		SystemStatusDto.SystemStatusDtoBuilder builder = SystemStatusDto.builder();
		String auth = env.getProperty(SELECTED_AUTH, SELECTED_AUTH_DB);
		builder.authMode(auth);
		builder.version(getVersion());
		builder.built(getBuildTime().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)));

		if (SELECTED_AUTH_LDAP.equals(auth)) {
			return builder.status(OK).build();
		}

		long result = userRepository.countExistingAdmins();
		return builder.status(result > 0 ? OK : "NEED_SETUP").build();
	}

	@Override
	@EventListener
	@CacheEvict(allEntries = true)
	public void refreshSystemStatus(RefreshCacheEvent userChangedEvent) {
		log.info("System status cache refreshed");
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
