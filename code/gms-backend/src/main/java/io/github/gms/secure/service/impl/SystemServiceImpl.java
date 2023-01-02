package io.github.gms.secure.service.impl;

import static io.github.gms.common.util.Constants.OK;
import static io.github.gms.common.util.Constants.SELECTED_AUTH;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_DB;
import static io.github.gms.common.util.Constants.SELECTED_AUTH_LDAP;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.github.gms.common.dto.SystemStatusDto;
import io.github.gms.common.event.RefreshCacheEvent;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.SystemService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "systemStatusCache")
public class SystemServiceImpl implements SystemService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private Environment env;

	@Override
	@Cacheable
	public SystemStatusDto getSystemStatus() {
		SystemStatusDto.SystemStatusDtoBuilder builder = SystemStatusDto.builder();
		String auth = env.getProperty(SELECTED_AUTH, SELECTED_AUTH_DB);
		builder.authMode(auth);
		
		if (SELECTED_AUTH_LDAP.equals(auth)) {
			return builder.status(OK).build();
		}

		long result = userRepository.countExistingAdmins();
		return  builder.status(result > 0 ? OK : "NEED_SETUP").build();
	}

	@Override
	@EventListener
	@CacheEvict(allEntries = true)
	public void refreshSystemStatus(RefreshCacheEvent userChangedEvent) {
		log.info("System status cache refreshed");
	}
}
