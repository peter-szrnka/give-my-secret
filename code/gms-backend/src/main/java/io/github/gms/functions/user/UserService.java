package io.github.gms.functions.user;

import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.common.dto.LongValueDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.dto.UserInfoDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.service.CountService;
import io.github.gms.common.service.JwtClaimService;
import io.github.gms.common.types.ErrorCode;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.MdcUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.gms.common.types.ErrorCode.GMS_003;
import static io.github.gms.common.types.ErrorCode.GMS_004;
import static io.github.gms.common.types.ErrorCode.GMS_005;
import static io.github.gms.common.util.Constants.ACCESS_JWT_TOKEN;
import static io.github.gms.common.util.Constants.CACHE_API;
import static io.github.gms.common.util.Constants.CACHE_USER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = { CACHE_USER })
public class UserService implements AbstractCrudService<SaveUserRequestDto, SaveEntityResponseDto, UserDto, UserListDto>, CountService {
	
	private static final String CREDENTIAL_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,255}$";

	private final UserRepository repository;
	private final UserConverter converter;
	private final PasswordEncoder passwordEncoder;
	private final JwtClaimService jwtClaimService;
	private final SecretGenerator secretGenerator;

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
	public UserListDto list(Pageable pageable) {
		Page<UserEntity> resultList = repository.findAll(pageable);
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

	@Cacheable(cacheNames = CACHE_USER)
	public String getUsernameById(Long id) {
		return getById(id).getUsername();
	}

	public void changePassword(ChangePasswordRequestDto dto) {
		UserEntity user = validateAndReturnUser(MdcUtils.getUserId());
		validateCredentials(user, dto);
		user.setCredential(passwordEncoder.encode(dto.getNewCredential()));
		repository.save(user);
	}

	public byte[] getMfaQrCode() throws QrGenerationException {
		UserEntity entity = validateAndReturnUser(MdcUtils.getUserId());
		QrData data = new QrData.Builder()
			.label(entity.getEmail())
			.secret(entity.getMfaSecret())
			.issuer("Give My Secret")
			.algorithm(HashingAlgorithm.SHA1)
			.digits(6)
			.period(30)
			.build();

		QrGenerator generator = new ZxingPngQrGenerator();
		return generator.generate(data);
	}

	public void toggleMfa(boolean enabled) {
		UserEntity entity = validateAndReturnUser(MdcUtils.getUserId());
		entity.setMfaEnabled(enabled);
		repository.save(entity);
	}

	public boolean isMfaActive() {
		UserEntity entity = validateAndReturnUser(MdcUtils.getUserId());
		return entity.isMfaEnabled();
	}

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
			.role(entity.getRole())
			.email(entity.getEmail())
			.build();
	}

	private SaveEntityResponseDto saveUser(SaveUserRequestDto dto, boolean roleChangeEnabled) {
		validateUserExistence(dto);

		UserEntity entity;

		if (dto.getId() == null) {
			entity = converter.toNewEntity(dto, roleChangeEnabled);

			// Generate an MFA secret for the user
			entity.setMfaSecret(secretGenerator.generate());
		} else {
			entity = converter.toEntity(repository.findById(dto.getId())
					.orElseThrow(() -> new GmsException("User entity not found!", ErrorCode.GMS_003)), dto, roleChangeEnabled);
		}

		entity = repository.save(entity);
		return new SaveEntityResponseDto(entity.getId());
	}
	
	private void validateUserExistence(SaveUserRequestDto dto) {
		if (dto.getId() != null) {
			return;
		}

		if(repository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail()).isPresent()) {
			throw new GmsException("User already exists!", GMS_003);
		}
	}

	private UserEntity validateAndReturnUser(Long userId) {
		return repository.findById(userId).orElseThrow(() -> {
			log.warn("User not found");
            return new GmsException("User not found!", ErrorCode.GMS_003);
		});
	}
	
	private void validateCredentials(UserEntity entity, ChangePasswordRequestDto dto) {
		if (!passwordEncoder.matches(dto.getOldCredential(), entity.getCredential())) {
			throw new GmsException("Old credential is not valid!", GMS_004);
		}

		Pattern pattern = Pattern.compile(CREDENTIAL_REGEX);
        Matcher matcher = pattern.matcher(dto.getNewCredential());

        if (!matcher.matches()) {
        	throw new GmsException("New credential is not valid! It must contain at least 1 lowercase, 1 uppercase and 1 numeric character.", GMS_005);
        }
	}
}
