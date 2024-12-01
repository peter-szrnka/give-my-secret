package io.github.gms.functions.systemproperty;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class SystemPropertyServiceTest extends AbstractUnitTest {

	private SystemPropertyConverter converter;
	private SystemPropertyRepository repository;
	private SystemPropertyService service;

	@BeforeEach
	public void setup() {
		converter = mock(SystemPropertyConverter.class);
		repository = mock(SystemPropertyRepository.class);
		service = new SystemPropertyService(converter, repository);
	}

	@Test
	void save_whenInvalidKeyProvided_thenThrowException() {
		// arrange
		SystemPropertyEntity mockEntity = TestUtils.createSystemPropertyEntity(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS, "900");
		SystemPropertyDto inputDto = SystemPropertyDto.builder().key(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS.name()).value("0").build();
		when(repository.findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(mockEntity);

		//act
		GmsException exception = assertThrows(GmsException.class, () -> service.save(inputDto));

		// assert
		assertThat(exception).hasMessage("Invalid value for system property!");
		verify(repository).findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		verify(converter, never()).toEntity(mockEntity, inputDto);
		verify(repository, never()).save(mockEntity);
	}
	
	@Test
	void save_whenNewSystemPropertyProvided_thenSaveSystemProperty() {
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
	void delete_whenInvalidKeyProvided_thenThrowException() {
		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.delete("Invalid key"));

		// assert
		assertEquals("Unknown system property!", exception.getMessage());
	}
	
	@Test
	void delete_whenInputProvided_thenDeleteSystemProperty() {
		// act
		service.delete(SystemProperty.ACCESS_JWT_ALGORITHM.name());

		// assert
		verify(repository).deleteByKey(SystemProperty.ACCESS_JWT_ALGORITHM);
	}
	
	@Test
	void list_whenInputExceptionOccurred_thenReturnResultList() {
		// arrange
		when(repository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Unexpected error!"));
		Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

		// act
		SystemPropertyListDto response = service.list(pageable);

		// assert
		assertNotNull(response);
		assertEquals(0, response.getResultList().size());
		assertEquals(0L, response.getTotalElements());
		verify(repository).findAll(any(Pageable.class));
		verify(converter, never()).toDtoList(any());
	}
	
	@Test
	void list_whenInputProvided_thenReturnResultList() {
		// arrange
		Page<SystemPropertyEntity> mockList = new PageImpl<>(Lists.newArrayList(new SystemPropertyEntity()));
		when(repository.findAll(any(Pageable.class))).thenReturn(mockList);
		SystemPropertyListDto mockDto = SystemPropertyListDto.builder()
				.resultList(Lists.newArrayList(SystemPropertyDto.builder().key("a").value("b").build()))
				.totalElements(1).build();
		when(converter.toDtoList(any())).thenReturn(mockDto);
		Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

		// act
		SystemPropertyListDto response = service.list(pageable);

		// assert
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		assertEquals(mockDto.getResultList().getFirst().toString(), response.getResultList().getFirst().toString());
		verify(repository).findAll(any(Pageable.class));
		verify(converter).toDtoList(any());
	}
	
	@Test
	void getLong_whenValueIsNotProvided_thenReturnDefaultValue() {
		// arrange
		when(repository.getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(Optional.empty());
		
		//act
		Long response = service.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		
		// assert
		assertEquals(900L, response);
		verify(repository).getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
	}
	
	@Test
	void getLong_whenValueProvided_thenReturnValue() {
		// arrange
		when(repository.getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(Optional.of("3600"));
		
		//act
		Long response = service.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		
		// assert
		assertEquals(3600L, response);
		verify(repository).getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
	}

	@Test
	void getInteger_whenValueProvided_thenReturnValue() {
		// arrange
		when(repository.getValueByKey(SystemProperty.FAILED_ATTEMPTS_LIMIT)).thenReturn(Optional.of("2"));

		// act
		Integer response = service.getInteger(SystemProperty.FAILED_ATTEMPTS_LIMIT);

		// assert
		assertEquals(2, response);
		verify(repository).getValueByKey(SystemProperty.FAILED_ATTEMPTS_LIMIT);
	}

	@Test
	void getInteger_whenValueIsNotProvided_thenReturnDefaultValue() {
		// arrange
		when(repository.getValueByKey(SystemProperty.FAILED_ATTEMPTS_LIMIT)).thenReturn(Optional.empty());

		// act
		Integer response = service.getInteger(SystemProperty.FAILED_ATTEMPTS_LIMIT);

		// assert
		assertEquals(3, response);
		verify(repository).getValueByKey(SystemProperty.FAILED_ATTEMPTS_LIMIT);
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void getBoolean_whenValueIsProvided_thenReturnBoolean(boolean value) {
		// arrange
		when(repository.getValueByKey(SystemProperty.ENABLE_GLOBAL_MFA)).thenReturn(Optional.of(String.valueOf(value)));

		//act
		Boolean response = service.getBoolean(SystemProperty.ENABLE_GLOBAL_MFA);

		// assert
		assertEquals(value, response);
		verify(repository).getValueByKey(SystemProperty.ENABLE_GLOBAL_MFA);
	}

	@Test
	void updateSystemProperty_whenValueIsValid_thenUpdateSystemProperty() {
		// arrange
		SystemPropertyEntity mockEntity = TestUtils.createSystemPropertyEntity(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS, "900");
		SystemPropertyDto inputDto = SystemPropertyDto.builder().key(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS.name()).value("900").build();
		when(repository.findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(mockEntity);
		when(converter.toEntity(mockEntity, inputDto)).thenReturn(mockEntity);

		//act
		assertDoesNotThrow(() -> service.updateSystemProperty(inputDto));

		// assert
		verify(repository).findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		ArgumentCaptor<SystemPropertyEntity> captor = ArgumentCaptor.forClass(SystemPropertyEntity.class);
		verify(converter).toEntity(mockEntity, inputDto);
		verify(repository).save(captor.capture());
		SystemPropertyEntity captured = captor.getValue();
		assertEquals("SystemPropertyEntity(id=null, key=ACCESS_JWT_EXPIRATION_TIME_SECONDS, value=900, lastModified=null)", captured.toString());
	}

	@Test
	void updateSystemProperty_whenValueIsInvalid_thenThrowException() {
		// arrange
		SystemPropertyEntity mockEntity = TestUtils.createSystemPropertyEntity(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS, "900");
		SystemPropertyDto inputDto = SystemPropertyDto.builder().key(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS.name()).value("0").build();
		when(repository.findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(mockEntity);

		//act
		GmsException exception = assertThrows(GmsException.class, () -> service.updateSystemProperty(inputDto));

		// assert
		assertThat(exception).hasMessage("Invalid value for system property!");
		verify(repository).findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		verify(converter, never()).toEntity(mockEntity, inputDto);
		verify(repository, never()).save(mockEntity);
	}
}
