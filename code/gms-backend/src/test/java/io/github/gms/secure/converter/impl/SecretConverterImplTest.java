package io.github.gms.secure.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

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
import io.github.gms.secure.dto.SaveSecretRequestDto;
import io.github.gms.secure.dto.SecretDto;
import io.github.gms.secure.dto.SecretListDto;
import io.github.gms.secure.entity.ApiKeyRestrictionEntity;
import io.github.gms.secure.entity.SecretEntity;
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
		originalEntity.setLastUpdated(ZonedDateTime.now(clock));

		SaveSecretRequestDto dto = new SaveSecretRequestDto();
		dto.setValue("value");
		dto.setRotationPeriod(RotationPeriod.DAILY);
		dto.setStatus(EntityStatus.DISABLED);
		dto.setKeystoreAliasId(1L);
		dto.setSecretId("secret");
		dto.setUserId(1L);
		dto.setRotationEnabled(true);
		dto.setReturnDecrypted(false);
		dto.setType(SecretType.SIMPLE_CREDENTIAL);

		// act
		SecretEntity entity = converter.toEntity(originalEntity, dto);

		// assert
		assertNotNull(entity);
		assertEquals("SecretEntity(id=1, userId=1, keystoreAliasId=1, secretId=secret, value=value, status=DISABLED, type=SIMPLE_CREDENTIAL, creationDate=2023-06-29T00:00Z, lastUpdated=2023-06-29T00:00Z, lastRotated=2023-06-29T00:00Z, rotationPeriod=DAILY, returnDecrypted=false, rotationEnabled=true)", entity.toString());
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
		dto.setReturnDecrypted(false);
		dto.setType(SecretType.SIMPLE_CREDENTIAL);

		// act
		SecretEntity entity = converter.toNewEntity(dto);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
		assertEquals(RotationPeriod.DAILY, entity.getRotationPeriod());
		assertEquals("value", entity.getValue());
		assertEquals("secret", entity.getSecretId());
		assertEquals(1L, entity.getUserId());
		assertTrue(entity.isRotationEnabled());
		assertFalse(entity.isReturnDecrypted());
		assertEquals(SecretType.SIMPLE_CREDENTIAL, entity.getType());
		assertNotNull(entity.getLastUpdated());
	}

	@Test
	void checkToList() {
		// arrange
		when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
		Page<SecretEntity> entityList = new PageImpl<>(Lists.newArrayList(TestUtils.createSecretEntity()));
		entityList.getContent().get(0).setCreationDate(ZonedDateTime.now(clock));
		entityList.getContent().get(0).setLastRotated(ZonedDateTime.now(clock));

		// act
		SecretListDto resultList = converter.toDtoList(entityList);

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
		assertEquals(1L, resultList.getTotalElements());
		assertEquals("SecretDto(id=1, userId=1, keystoreId=null, keystoreAliasId=1, secretId=secret, status=ACTIVE, type=SIMPLE_CREDENTIAL, creationDate=2023-06-29T00:00Z, lastUpdated=null, lastRotated=2023-06-29T00:00Z, rotationPeriod=YEARLY, returnDecrypted=false, rotationEnabled=false, apiKeyRestrictions=null)", resultList.getResultList().get(0).toString());
	}
	
	@Test
	void checkToDto() {
		// arrange
		SecretEntity entity = TestUtils.createSecretEntity();
		ApiKeyRestrictionEntity apiKeyRestrictionEntity1 = new ApiKeyRestrictionEntity();
		apiKeyRestrictionEntity1.setApiKeyId(1L);
		
		// act
		SecretDto response = converter.toDto(entity, List.of(apiKeyRestrictionEntity1));
		
		// assert
		assertNotNull(response);
		assertFalse(response.getApiKeyRestrictions().isEmpty());
		assertTrue(response.getApiKeyRestrictions().contains(1L));

		assertEquals(EntityStatus.ACTIVE, response.getStatus());
		assertEquals(RotationPeriod.YEARLY, response.getRotationPeriod());
		assertEquals("secret", response.getSecretId());
		assertEquals(1L, response.getUserId());
		assertFalse(response.isRotationEnabled());
		assertFalse(response.isReturnDecrypted());
		assertEquals(SecretType.SIMPLE_CREDENTIAL, entity.getType());
		assertNull(response.getLastUpdated());
	}
}
