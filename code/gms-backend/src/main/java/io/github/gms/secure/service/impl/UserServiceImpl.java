package io.github.gms.secure.service.impl;

import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.enums.UserRole;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.common.util.MdcUtils;
import io.github.gms.secure.converter.UserConverter;
import io.github.gms.secure.dto.ChangePasswordRequestDto;
import io.github.gms.secure.dto.LongValueDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveUserRequestDto;
import io.github.gms.secure.dto.UserDto;
import io.github.gms.secure.dto.UserInfoDto;
import io.github.gms.secure.dto.UserListDto;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.JwtClaimService;
import io.github.gms.secure.service.SystemPropertyService;
import io.github.gms.secure.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.CACHE_USER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@CacheConfig(cacheNames = { CACHE_USER, CACHE_API })
public class UserServiceImpl implements UserService {
	
	private static final String CREDENTIAL_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,255}$";

	private final UserRepository repository;
	private final UserConverter converter;
	private final PasswordEncoder passwordEncoder;
	private final JwtClaimService jwtClaimService;
	private final SystemPropertyService systemPropertyService;

	public UserServiceImpl(UserRepository repository, UserConverter converter,
                           PasswordEncoder passwordEncoder, JwtClaimService jwtClaimService, SystemPropertyService systemPropertyService) {
		this.repository = repository;
		this.converter = converter;
		this.passwordEncoder = passwordEncoder;
		this.jwtClaimService = jwtClaimService;
        this.systemPropertyService = systemPropertyService;
    }
	
	@Override
	@Transactional
	public SaveEntityResponseDto saveAdminUser(SaveUserRequestDto dto) {
		log.info("service saveUser called");
		return saveUser(dto, true);
	}

	@Override
	@CacheEvict(cacheNames = { CACHE_USER, CACHE_API }, allEntries = true)
	public SaveEntityResponseDto save(SaveUserRequestDto dto) {
		boolean isAdmin = Boolean.parseBoolean(MDC.get(MdcParameter.IS_ADMIN.getDisplayName()));
		return saveUser(dto, isAdmin);
	}

	@Override
	public UserDto getById(Long id) {
		return converter.toDto(validateAndReturnUser(id));
	}

	@Override
	public UserListDto list(PagingDto dto) {
		Page<UserEntity> resultList = repository.findAll(ConverterUtils.createPageable(dto));
		return converter.toDtoList(resultList);
	}

	@Override
	@CacheEvict(cacheNames = { CACHE_USER, CACHE_API }, allEntries = true)
	public void delete(Long id) {
		validateAndReturnUser(id);
		repository.deleteById(id);
	}
	
	@Override
	@CacheEvict(cacheNames = { CACHE_USER, CACHE_API }, allEntries = true)
	public void toggleStatus(Long id, boolean enabled) {
		UserEntity entity = validateAndReturnUser(id);
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
		UserEntity user = validateAndReturnUser(MdcUtils.getUserId());
		validateCredentials(user, dto);
		user.setCredential(passwordEncoder.encode(dto.getNewCredential()));
		repository.save(user);
	}

	@Override
	public byte[] getMfaQrCode() throws QrGenerationException {
		UserEntity entity = validateAndReturnUser(MdcUtils.getUserId());
		QrData data = new QrData.Builder()
			.label(entity.getEmail())
			.secret(entity.getMfaSecret())
			.issuer("Give My Secret")
			.algorithm(HashingAlgorithm.SHA1) // More on this below
			.digits(6)
			.period(30)
			.build();

		QrGenerator generator = new ZxingPngQrGenerator();
		return generator.generate(data);
	}

	@Override
	public void toggleMfa(boolean enabled) {
		UserEntity entity = validateAndReturnUser(MdcUtils.getUserId());
		entity.setMfaEnabled(enabled);
		repository.save(entity);
	}

	@Override
	public boolean isMfaActive() {
		UserEntity entity = validateAndReturnUser(MdcUtils.getUserId());
		return entity.isMfaEnabled();
	}

	@Override
	public UserInfoDto getUserInfo(HttpServletRequest request) {
		Cookie jwtTokenCookie = WebUtils.getCookie(request, ACCESS_JWT_TOKEN);

		if (jwtTokenCookie == null) {
			// We should not return an error, just simply return nothing
			return null;
		}

		Claims claims = jwtClaimService.getClaims(jwtTokenCookie.getValue());
		UserEntity entity = validateAndReturnUser(claims.get(MdcParameter.USER_ID.getDisplayName(), Long.class));
		return UserInfoDto.builder()
			.id(entity.getId())
			.name(entity.getName())
			.username(entity.getUsername())
			.roles(Stream.of(entity.getRoles().split(";")).map(UserRole::getByName).collect(Collectors.toSet()))
			.email(entity.getEmail())
			.build();
	}

	@Override
	public void updateLoginAttempt(String username) {
		UserEntity user = getByUsername(username);

		if (EntityStatus.BLOCKED == user.getStatus()) {
			log.info("User already blocked");
			return;
		}

		Integer attemptsLimit = systemPropertyService.getInteger(SystemProperty.FAILED_ATTEMPTS_LIMIT);
		Integer failedAttempts = user.getFailedAttempts() + 1;
		if (Objects.equals(attemptsLimit, failedAttempts)) {
			user.setStatus(EntityStatus.BLOCKED);
		}

		user.setFailedAttempts(failedAttempts);
		repository.save(user);
	}

	@Override
	public void resetLoginAttempt(String username) {
		UserEntity user = getByUsername(username);
		user.setFailedAttempts(0);
		repository.save(user);
	}

	@Override
	public boolean isBlocked(String username) {
		return EntityStatus.BLOCKED == getByUsername(username).getStatus();
	}

	private SaveEntityResponseDto saveUser(SaveUserRequestDto dto, boolean roleChangeEnabled) {
		validateUserExistence(dto);

		UserEntity entity;

		if (dto.getId() == null) {
			entity = converter.toNewEntity(dto, roleChangeEnabled);

			// Generate an MFA secret for the user
			SecretGenerator secretGenerator = new DefaultSecretGenerator();
			entity.setMfaSecret(secretGenerator.generate());
		} else {
			entity = converter.toEntity(repository.findById(dto.getId())
					.orElseThrow(() -> new GmsException("User entity not found!")), dto, roleChangeEnabled);
		}

		entity = repository.save(entity);
		return new SaveEntityResponseDto(entity.getId());
	}

	private UserEntity getByUsername(String username) {
		return repository.findByUsername(username).orElseThrow(() -> new GmsException("User not found!"));
	}
	
	private void validateUserExistence(SaveUserRequestDto dto) {
		if (dto.getId() != null) {
			return;
		}

		if(repository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail()).isPresent()) {
			throw new GmsException("User already exists!");
		}
	}

	private UserEntity validateAndReturnUser(Long userId) {
		return repository.findById(userId).orElseThrow(() -> {
			log.warn("User not found");
            return new GmsException("User not found!");
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
