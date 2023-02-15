package io.github.gms.secure.converter.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.secure.converter.KeystoreConverter;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.entity.KeystoreEntity;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link KeystoreConverterImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class KeystoreConverterImplTest extends AbstractUnitTest {

	private static final String FILE_NAME = "filename.txt";
	@Mock
	private Clock clock;
	@InjectMocks
	private KeystoreConverter converter = new KeystoreConverterImpl();
	
	@Test
	void checkToEntityWithoutFile() {
		// act
		KeystoreEntity entity = converter.toEntity(TestUtils.createKeystoreEntity(), TestUtils.createSaveKeystoreRequestDto(), null);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
		assertEquals("test.jks", entity.getFileName());
	}

	@Test
	void checkToEntityWithParameters() {
		// arrange
		MultipartFile multipartFile = mock(MultipartFile.class);
		when(multipartFile.getOriginalFilename()).thenReturn(FILE_NAME);

		// act
		KeystoreEntity entity = converter.toEntity(TestUtils.createKeystoreEntity(), TestUtils.createSaveKeystoreRequestDto(), multipartFile);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
		assertEquals(FILE_NAME, entity.getFileName());
		verify(multipartFile).getOriginalFilename();
	}
	
	@Test
	void checkToNewEntityWithoutFile() {
		// arrange
		setupClock(clock);

		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();

		// act
		KeystoreEntity entity = converter.toNewEntity(dto, null);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
		assertNull(entity.getFileName());
	}

	@Test
	void checkToNewEntity() {
		// arrange
		setupClock(clock);

		// arrange
		SaveKeystoreRequestDto dto = TestUtils.createSaveKeystoreRequestDto();
		MultipartFile multipartFile = mock(MultipartFile.class);
		when(multipartFile.getOriginalFilename()).thenReturn(FILE_NAME);

		// act
		KeystoreEntity entity = converter.toNewEntity(dto, multipartFile);

		// assert
		assertNotNull(entity);
		assertEquals(EntityStatus.ACTIVE, entity.getStatus());
		assertEquals(FILE_NAME, entity.getFileName());
		verify(multipartFile).getOriginalFilename();
	}

	@Test
	void checkToList() {
		// arrange
		Page<KeystoreEntity> entityList = new PageImpl<>(Lists.newArrayList(TestUtils.createKeystoreEntity()));

		// act
		KeystoreListDto resultList = converter.toDtoList(entityList);

		// assert
		assertNotNull(resultList);
		assertEquals(1, resultList.getResultList().size());
	}
}
