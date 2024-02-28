package io.github.gms.functions.secret;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import io.github.gms.functions.secret.SecretConverterImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.google.common.collect.Lists;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.RotationPeriod;
import io.github.gms.common.enums.SecretType;
import io.github.gms.functions.secret.SaveSecretRequestDto;
import io.github.gms.functions.secret.SecretDto;
import io.github.gms.functions.secret.SecretListDto;
import io.github.gms.functions.secret.ApiKeyRestrictionEntity;
import io.github.gms.functions.secret.SecretEntity;
import io.github.gms.util.TestUtils;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SecretConverterImplTest extends AbstractUnitTest {

	@Mock
	private Clock clock;
	@InjectMocks
	private SecretConverterImpl converter;

	@BeforeEach
	void beforeEach() {
		clock = mock(Clock.class);
		converter = new SecretConverterImpl(clock);
	}

	@Test
	void checkToEntityWithoutParameters() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);

		SecretEntity originalEntity = TestUtils.createSecretEntity();
		originalEntity.setCreationDate(ZonedDateTime.now(clock));
		originalEntity.setLastRotated(ZonedDateTime.now(clock));
		originalEntity.setLastUpdated(ZonedDateTime.now(clock));
		SecretEntity entity = converter.toEntity(originalEntity, new SaveSecretRequestDto());

		// assert
		assertNotNull(entity);
		assertEquals("SecretEntity(id=1, userId=null, keystoreAliasId=null, secretId=null, value=test, status=ACTIVE, type=null, creationDate=2023-06-29T00:00Z, lastUpdated=2023-06-29T00:00Z, lastRotated=2023-06-29T00:00Z, rotationPeriod=YEARLY, returnDecrypted=false, rotationEnabled=false)", entity.toString());
	}

	@Test
	void checkToEntityWithParameters() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);

		SecretEntity originalEntity = TestUtils.createSecretEntity();
		originalEntity.setCreationDate(ZonedDateTime.now(clock));
		originalEntity.setLastRotated(ZonedDateTime.now(clock));

		SaveSecretRequestDto dto = new SaveSecretRequestDto();
		dto.setValue("value");
		dto.setRotationPeriod(RotationPeriod.DAILY);
		dto.setStatus(EntityStatus.DISABLED);
		dto.setKeystoreAliasId(1L);
		dto.setSecretId("secret");
		dto.setUserId(1L);
		dto.setRotationEnabled(true);
		dto.setReturnDecrypted(true);
		dto.setType(SecretType.SIMPLE_CREDENTIAL);

		// act
		SecretEntity entity = converter.toEntity(originalEntity, dto);

		// assert
		assertNotNull(entity);
		assertEquals("SecretEntity(id=1, userId=1, keystoreAliasId=1, secretId=secret, value=value, status=DISABLED, type=SIMPLE_CREDENTIAL, creationDate=2023-06-29T00:00Z, lastUpdated=2023-06-29T00:00Z, lastRotated=2023-06-29T00:00Z, rotationPeriod=DAILY, returnDecrypted=true, rotationEnabled=true)", entity.toString());
	}

	@Test
	void checkToNewEntity() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);

		// arrange
		SaveSecretRequestDto dto = new SaveSecretRequestDto();
		dto.setValue("value");
		dto.setRotationPeriod(RotationPeriod.DAILY);
		dto.setStatus(EntityStatus.DISABLED);
		dto.setKeystoreAliasId(1L);
		dto.setSecretId("secret");
		dto.setUserId(1L);
		dto.setRotationEnabled(true);
		dto.setReturnDecrypted(true);
		dto.setType(SecretType.SIMPLE_CREDENTIAL);

		// act
		SecretEntity entity = converter.toNewEntity(dto);

		// assert
		assertNotNull(entity);
		assertEquals("SecretEntity(id=null, userId=1, keystoreAliasId=1, secretId=secret, value=value, status=ACTIVE, type=SIMPLE_CREDENTIAL, creationDate=2023-06-29T00:00Z, lastUpdated=2023-06-29T00:00Z, lastRotated=2023-06-29T00:00Z, rotationPeriod=DAILY, returnDecrypted=true, rotationEnabled=true)", entity.toString());
	}

	@Test
	void checkToList() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		SecretEntity mockEntity = TestUtils.createSecretEntity();
		mockEntity.setCreationDate(ZonedDateTime.now(clock));
		mockEntity.setLastRotated(ZonedDateTime.now(clock));
		mockEntity.setReturnDecrypted(true);
		mockEntity.setRotationEnabled(true);
		Page<SecretEntity> entityList = new PageImpl<>(Lists.newArrayList(mockEntity));

		// act
		SecretListDto resultList = converter.toDtoList(entityList);

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
		assertEquals(1L, resultList.getTotalElements());
		assertEquals("SecretDto(id=1, userId=1, keystoreId=null, keystoreAliasId=1, secretId=secret, status=ACTIVE, type=SIMPLE_CREDENTIAL, creationDate=2023-06-29T00:00Z, lastUpdated=null, lastRotated=2023-06-29T00:00Z, rotationPeriod=YEARLY, returnDecrypted=true, rotationEnabled=true, apiKeyRestrictions=null)", resultList.getResultList().get(0).toString());
	}
	
	@Test
	void checkToDto() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		SecretEntity entity = TestUtils.createSecretEntity();
		entity.setCreationDate(ZonedDateTime.now(clock));
		entity.setLastRotated(ZonedDateTime.now(clock));
		entity.setLastUpdated(ZonedDateTime.now(clock));
		ApiKeyRestrictionEntity apiKeyRestrictionEntity1 = new ApiKeyRestrictionEntity();
		apiKeyRestrictionEntity1.setApiKeyId(1L);
		
		// act
		SecretDto response = converter.toDto(entity, List.of(apiKeyRestrictionEntity1));
		
		// assert
		assertNotNull(response);
		assertEquals("SecretDto(id=1, userId=1, keystoreId=null, keystoreAliasId=1, secretId=secret, status=ACTIVE, type=SIMPLE_CREDENTIAL, creationDate=2023-06-29T00:00Z, lastUpdated=2023-06-29T00:00Z, lastRotated=2023-06-29T00:00Z, rotationPeriod=YEARLY, returnDecrypted=false, rotationEnabled=false, apiKeyRestrictions=[1])", response.toString());
	}
}
