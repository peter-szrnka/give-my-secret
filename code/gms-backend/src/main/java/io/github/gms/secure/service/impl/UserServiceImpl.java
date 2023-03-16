package io.github.gms.secure.service.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.event.RefreshCacheEvent;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.dto.ChangePasswordRequestDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserDto;
import io.github.gms.secure.dto.UserListDto;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "userCache")
public class UserServiceImpl implements UserService {
	
	private static final String CREDENTIAL_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,255}$";

	@Autowired
	private UserRepository repository;

	@Autowired
	private UserConverter converter;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	@Transactional
	public SaveEntityResponseDto saveAdminUser(SaveUserRequestDto dto) {
		log.info("service saveUser called");
		return saveUser(dto, true);
	}

	@Override
	@CacheEvict(cacheNames = "userCache")
	public SaveEntityResponseDto save(SaveUserRequestDto dto) {
		boolean isAdmin = Boolean.parseBoolean(MDC.get(MdcParameter.IS_ADMIN.getDisplayName()));
		return saveUser(dto, isAdmin);
	}

	@Override
	public UserDto getById(Long id) {
		return converter.toDto(validateUser(id));
	}

	@Override
	public UserListDto list(PagingDto dto) {
		Page<UserEntity> resultList = repository.findAll(ConverterUtils.createPageable(dto));
		return converter.toDtoList(resultList);
	}

	@Override
	@CacheEvict(cacheNames = "userCache")
	public void delete(Long id) {
		validateUser(id);
		repository.deleteById(id);
	}
	
	@Override
	@CacheEvict(cacheNames = "userCache")
	public void toggleStatus(Long id, boolean enabled) {
		UserEntity entity = validateUser(id);
		entity.setStatus(enabled ? EntityStatus.ACTIVE : EntityStatus.DISABLED);
		repository.save(entity);
	}

	@Override
	public LongValueDto count() {
		return new LongValueDto(repository.countNormalUsers());
	}

	@Override
	@Cacheable
	public String getUsernameById(Long id) {
		return getById(id).getUsername();
	}
	

	@Override
	public void changePassword(ChangePasswordRequestDto dto) {
		Long userId = Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
		UserEntity user = validateUser(userId);
		validateCredentials(user, dto);
		user.setCredential(passwordEncoder.encode(dto.getNewCredential()));
		repository.save(user);
	}
	
	private SaveEntityResponseDto saveUser(SaveUserRequestDto dto, boolean roleChangeEnabled) {
		validateUserExistence(dto);

		UserEntity entity;

		if (dto.getId() == null) {
			entity = converter.toNewEntity(dto, roleChangeEnabled);
		} else {
			entity = converter.toEntity(repository.findById(dto.getId())
					.orElseThrow(() -> new GmsException("User entity not found!")), dto, roleChangeEnabled);
		}

		entity = repository.save(entity);

		if (entity.getRoles().contains(UserRole.ROLE_ADMIN.name())) {
			applicationEventPublisher.publishEvent(new RefreshCacheEvent(this));
		}

		return new SaveEntityResponseDto(entity.getId());
	}
	
	private void validateUserExistence(SaveUserRequestDto dto) {
		if (dto.getId() != null) {
			return;
		}

		if(repository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail()).isPresent()) {
			throw new GmsException("User already exists!");
		}
	}

	private UserEntity validateUser(Long userId) {
		return repository.findById(userId).orElseThrow(() -> {
			log.warn("User not found");
			throw new GmsException("User not found!");
		});
	}
	
	private void validateCredentials(UserEntity entity, ChangePasswordRequestDto dto) {
		if (!passwordEncoder.matches(dto.getOldCredential(), entity.getCredential())) {
			throw new GmsException("Old credential is not valid!");
		}

		Pattern pattern = Pattern.compile(CREDENTIAL_REGEX);
        Matcher matcher = pattern.matcher(dto.getNewCredential());

        if (!matcher.matches()) {
        	throw new GmsException("New credential is not valid! It must contain at least 1 lowercase, 1 uppercase and 1 numeric character.");
        }
	}
}
