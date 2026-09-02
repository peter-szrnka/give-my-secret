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
	void setup() {
		converter = mock(SystemPropertyConverter.class);
		repository = mock(SystemPropertyRepository.class);
		service = new SystemPropertyService(converter, repository);
	}

	@Test
	void save_whenInvalidKeyProvided_thenThrowException() {
		// given
		SystemPropertyEntity mockEntity = TestUtils.createSystemPropertyEntity(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS, "900");
		SystemPropertyDto inputDto = SystemPropertyDto.builder().key(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS.name()).value("0").build();
		when(repository.findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(mockEntity);

		//when
		GmsException exception = assertThrows(GmsException.class, () -> service.save(inputDto));

		// then
		assertThat(exception).hasMessage("Invalid value for system property!");
		verify(repository).findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		verify(converter, never()).toEntity(mockEntity, inputDto);
		verify(repository, never()).save(mockEntity);
	}
	
	@Test
	void save_whenNewSystemPropertyProvided_thenSaveSystemProperty() {
		// given
		SystemPropertyEntity mockEntity = TestUtils.createSystemPropertyEntity(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS, "900");
		SystemPropertyDto inputDto = SystemPropertyDto.builder().key(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS.name()).value("900").build();
		when(repository.findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(mockEntity);
		when(converter.toEntity(mockEntity, inputDto)).thenReturn(mockEntity);
		
		//when
		assertDoesNotThrow(() -> service.save(inputDto));
		
		// then
		verify(repository).findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		ArgumentCaptor<SystemPropertyEntity> captor = ArgumentCaptor.forClass(SystemPropertyEntity.class);
		verify(converter).toEntity(mockEntity, inputDto);
		verify(repository).save(captor.capture());
		SystemPropertyEntity captured = captor.getValue();
		assertEquals("SystemPropertyEntity(id=null, key=ACCESS_JWT_EXPIRATION_TIME_SECONDS, value=900, lastModified=null)", captured.toString());
	}
	
	@Test
	void delete_whenInvalidKeyProvided_thenThrowException() {
		// when
		GmsException exception = assertThrows(GmsException.class, () -> service.delete("Invalid key"));

		// then
		assertEquals("Unknown system property!", exception.getMessage());
	}
	
	@Test
	void delete_whenInputProvided_thenDeleteSystemProperty() {
		// when
		service.delete(SystemProperty.ACCESS_JWT_ALGORITHM.name());

		// then
		verify(repository).deleteByKey(SystemProperty.ACCESS_JWT_ALGORITHM);
	}
	
	@Test
	void list_whenInputExceptionOccurred_thenReturnResultList() {
		// given
		when(repository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Unexpected error!"));
		Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

		// when
		SystemPropertyListDto response = service.list(pageable);

		// then
		assertNotNull(response);
		assertEquals(0, response.getResultList().size());
		assertEquals(0L, response.getTotalElements());
		verify(repository).findAll(any(Pageable.class));
		verify(converter, never()).toDtoList(any());
	}
	
	@Test
	void list_whenInputProvided_thenReturnResultList() {
		// given
		Page<SystemPropertyEntity> mockList = new PageImpl<>(Lists.newArrayList(new SystemPropertyEntity()));
		when(repository.findAll(any(Pageable.class))).thenReturn(mockList);
		SystemPropertyListDto mockDto = SystemPropertyListDto.builder()
				.resultList(Lists.newArrayList(SystemPropertyDto.builder().key("a").value("b").build()))
				.totalElements(1).build();
		when(converter.toDtoList(any())).thenReturn(mockDto);
		Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

		// when
		SystemPropertyListDto response = service.list(pageable);

		// then
		assertNotNull(response);
		assertEquals(1, response.getResultList().size());
		assertEquals(mockDto.getResultList().getFirst().toString(), response.getResultList().getFirst().toString());
		verify(repository).findAll(any(Pageable.class));
		verify(converter).toDtoList(any());
	}
	
	@Test
	void getLong_whenValueIsNotProvided_thenReturnDefaultValue() {
		// given
		when(repository.getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(Optional.empty());
		
		//when
		Long response = service.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		
		// then
		assertEquals(900L, response);
		verify(repository).getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
	}
	
	@Test
	void getLong_whenValueProvided_thenReturnValue() {
		// given
		when(repository.getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(Optional.of("3600"));
		
		//when
		Long response = service.getLong(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		
		// then
		assertEquals(3600L, response);
		verify(repository).getValueByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
	}

	@Test
	void getInteger_whenValueProvided_thenReturnValue() {
		// given
		when(repository.getValueByKey(SystemProperty.FAILED_ATTEMPTS_LIMIT)).thenReturn(Optional.of("2"));

		// when
		Integer response = service.getInteger(SystemProperty.FAILED_ATTEMPTS_LIMIT);

		// then
		assertEquals(2, response);
		verify(repository).getValueByKey(SystemProperty.FAILED_ATTEMPTS_LIMIT);
	}

	@Test
	void getInteger_whenValueIsNotProvided_thenReturnDefaultValue() {
		// given
		when(repository.getValueByKey(SystemProperty.FAILED_ATTEMPTS_LIMIT)).thenReturn(Optional.empty());

		// when
		Integer response = service.getInteger(SystemProperty.FAILED_ATTEMPTS_LIMIT);

		// then
		assertEquals(3, response);
		verify(repository).getValueByKey(SystemProperty.FAILED_ATTEMPTS_LIMIT);
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void getBoolean_whenValueIsProvided_thenReturnBoolean(boolean value) {
		// given
		when(repository.getValueByKey(SystemProperty.ENABLE_GLOBAL_MFA)).thenReturn(Optional.of(String.valueOf(value)));

		//when
		Boolean response = service.getBoolean(SystemProperty.ENABLE_GLOBAL_MFA);

		// then
		assertEquals(value, response);
		verify(repository).getValueByKey(SystemProperty.ENABLE_GLOBAL_MFA);
	}

	@Test
	void updateSystemProperty_whenValueIsValid_thenUpdateSystemProperty() {
		// given
		SystemPropertyEntity mockEntity = TestUtils.createSystemPropertyEntity(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS, "900");
		SystemPropertyDto inputDto = SystemPropertyDto.builder().key(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS.name()).value("900").build();
		when(repository.findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(mockEntity);
		when(converter.toEntity(mockEntity, inputDto)).thenReturn(mockEntity);

		//when
		assertDoesNotThrow(() -> service.updateSystemProperty(inputDto));

		// then
		verify(repository).findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		ArgumentCaptor<SystemPropertyEntity> captor = ArgumentCaptor.forClass(SystemPropertyEntity.class);
		verify(converter).toEntity(mockEntity, inputDto);
		verify(repository).save(captor.capture());
		SystemPropertyEntity captured = captor.getValue();
		assertEquals("SystemPropertyEntity(id=null, key=ACCESS_JWT_EXPIRATION_TIME_SECONDS, value=900, lastModified=null)", captured.toString());
	}

	@Test
	void updateSystemProperty_whenValueIsInvalid_thenThrowException() {
		// given
		SystemPropertyEntity mockEntity = TestUtils.createSystemPropertyEntity(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS, "900");
		SystemPropertyDto inputDto = SystemPropertyDto.builder().key(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS.name()).value("0").build();
		when(repository.findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS)).thenReturn(mockEntity);

		//when
		GmsException exception = assertThrows(GmsException.class, () -> service.updateSystemProperty(inputDto));

		// then
		assertThat(exception).hasMessage("Invalid value for system property!");
		verify(repository).findByKey(SystemProperty.ACCESS_JWT_EXPIRATION_TIME_SECONDS);
		verify(converter, never()).toEntity(mockEntity, inputDto);
		verify(repository, never()).save(mockEntity);
	}
}

