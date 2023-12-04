package io.github.gms.secure.service.impl;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.converter.SystemPropertyConverter;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SystemPropertyDto;
import io.github.gms.secure.dto.SystemPropertyListDto;
import io.github.gms.secure.entity.SystemPropertyEntity;
import io.github.gms.secure.repository.SystemPropertyRepository;
import io.github.gms.util.TestUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SystemPropertyServiceImplTest extends AbstractUnitTest {

	private SystemPropertyConverter converter;
	private SystemPropertyRepository repository;
	private SystemPropertyServiceImpl service;

	@BeforeEach
	public void setup() {
		converter = mock(SystemPropertyConverter.class);
		repository = mock(SystemPropertyRepository.class);
		service = new SystemPropertyServiceImpl(converter, repository);
	}
	
	@Test
	void shouldSaveNewSystemProperty() {
		// arrange
		SystemPropertyEntity mockEntity = TestUtils.createSystemPropertyEntity(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS, "900");
		SystemPropertyDto inputDto = SystemPropertyDto.builder().key(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS.name()).value("900").build();
		when(repository.findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(mockEntity);
		when(converter.toEntity(mockEntity, inputDto)).thenReturn(mockEntity);
		
		//act
		assertDoesNotThrow(() -> service.save(inputDto));
		
		// assert
		verify(repository).findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		ArgumentCaptor<SystemPropertyEntity> captor = ArgumentCaptor.forClass(SystemPropertyEntity.class);
		verify(converter).toEntity(mockEntity, inputDto);
		verify(repository).save(captor.capture());
		SystemPropertyEntity captured = captor.getValue();
		assertEquals("SystemPropertyEntity(id=null, key=ACCESS_JWT_EXPIRATION_TIME_SECONDS, value=900, lastModified=null)", captured.toString());
	}
	
	@Test
	void shouldNotDelete() {
		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.delete("Invalid key"));

		// assert
		assertEquals("Unknown system property!", exception.getMessage());
	}
	
	@Test
	void shouldDelete() {
		// act
		service.delete(SystemProperty.ACCESS_JWT_ALGORITHM.name());

		// assert
		verify(repository).deleteByKey(SystemProperty.ACCESS_JWT_ALGORITHM);
	}
	
	@Test
	void shouldReturnEmptyList() {
		// arrange
		when(repository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Unexpected error!"));

		// act
		SystemPropertyListDto response = service.list(new PagingDto("ASC", "id", 0, 10));

		// assert
		assertNotNull(response);
		assertEquals(0, response.getResultList().size());
		assertEquals(0L, response.getTotalElements());
		verify(repository).findAll(any(Pageable.class));
		verify(converter, never()).toDtoList(any());
	}
	
	@Test
	void shouldReturnList() {
		// arrange
		Page<SystemPropertyEntity> mockList = new PageImpl<>(Lists.newArrayList(new SystemPropertyEntity()));
		when(repository.findAll(any(Pageable.class))).thenReturn(mockList);
		SystemPropertyListDto mockDto = SystemPropertyListDto.builder()
				.resultList(Lists.newArrayList(SystemPropertyDto.builder().key("a").value("b").build()))
				.totalElements(1).build();
		when(converter.toDtoList(any())).thenReturn(mockDto);

		// act
		SystemPropertyListDto response = service.list(new PagingDto("ASC", "id", 0, 10));

		// assert
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		assertEquals(mockDto.getResultList().get(0).toString(), response.getResultList().get(0).toString());
		verify(repository).findAll(any(Pageable.class));
		verify(converter).toDtoList(any());
	}
	
	@Test
	void shouldGetDefaultLongValue() {
		// arrange
		when(repository.getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(Optional.empty());
		
		//act
		Long response = service.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		
		// assert
		assertEquals(900L, response);
		verify(repository).getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
	}
	
	@Test
	void shouldGetLongValue() {
		// arrange
		when(repository.getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(Optional.of("3600"));
		
		//act
		Long response = service.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		
		// assert
		assertEquals(3600L, response);
		verify(repository).getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
	}

	@Test
	void shouldGetBooleanValue() {
		// arrange
		when(repository.getValueByKey(SystemProperty.ENABLE_GLOBAL_MFA)).thenReturn(Optional.of("true"));

		//act
		Boolean response = service.getBoolean(SystemProperty.ENABLE_GLOBAL_MFA);

		// assert
		assertEquals(true, response);
		verify(repository).getValueByKey(SystemProperty.ENABLE_GLOBAL_MFA);
	}
}
