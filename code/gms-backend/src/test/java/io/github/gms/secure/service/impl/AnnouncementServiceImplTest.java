package io.github.gms.secure.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.jboss.logging.MDC;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;
import io.github.gms.common.util.Constants;
import io.github.gms.secure.dto.AnnouncementDto;
import io.github.gms.secure.dto.AnnouncementListDto;
import io.github.gms.secure.dto.PagingDto;
import io.github.gms.secure.dto.SaveAnnouncementDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.entity.AnnouncementEntity;
import io.github.gms.secure.repository.AnnouncementRepository;
import io.github.gms.secure.service.UserService;
import io.github.gms.util.TestUtils;

/**
 * Unit test of {@link AnnouncementServiceImpl}
 * 
 * @author Peter Szrnka
 * @since 1.0
 */
class AnnouncementServiceImplTest extends AbstractUnitTest {

	@InjectMocks
	private AnnouncementServiceImpl service;
	
	@Mock
	private AnnouncementRepository repository;
	
	@Mock
	private UserService userService;
	
	@Test
	void shouldSaveNewEntity() {
		// arrange
		setupClock();
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		SaveAnnouncementDto dto = SaveAnnouncementDto.builder()
				.author("author")
				.description("description")
				.title("title")
				.build();
		when(repository.save(any(AnnouncementEntity.class))).thenReturn(TestUtils.createAnnouncementEntity(1L));

		// act
		SaveEntityResponseDto response = service.save(dto);
		
		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		
		MDC.clear();
	}
	
	@Test
	void shouldSaveExistingEntity() {
		// arrange
		MDC.put(MdcParameter.USER_ID.getDisplayName(), 1L);
		SaveAnnouncementDto dto = SaveAnnouncementDto.builder()
				.id(1L)
				.author("author")
				.description("description")
				.title("title")
				.build();
		when(repository.save(any(AnnouncementEntity.class))).thenReturn(TestUtils.createAnnouncementEntity(1L));

		// act
		SaveEntityResponseDto response = service.save(dto);
		
		// assert
		assertNotNull(response);
		assertEquals(1L, response.getEntityId());
		
		MDC.clear();
	}
	
	@Test
	void shouldReturnList() {
		// arrange
		when(repository.findAll(any(Pageable.class))).thenReturn(TestUtils.createAnnouncementEntityList());
		when(userService.getUsernameById(anyLong())).thenReturn("myuser");
		
		// act
		AnnouncementListDto response = service.list(new PagingDto("ASC", "id", 0, 10));
		
		// assert
		assertNotNull(response);
		assertFalse(response.getResultList().isEmpty());
		assertEquals("myuser", response.getResultList().get(0).getAuthor());
		
		verify(repository).findAll(any(Pageable.class));
		verify(userService).getUsernameById(anyLong());
	}
	
	@Test
	void shouldNotFindById() {
		// arrange
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		// act
		GmsException exception = assertThrows(GmsException.class, () -> service.getById(1L));

		// assert
		assertEquals(Constants.ENTITY_NOT_FOUND, exception.getMessage());
		verify(repository).findById(anyLong());
	}

	@Test
	void shouldFindById() {
		// arrange
		when(repository.findById(anyLong())).thenReturn(Optional.of(TestUtils.createAnnouncementEntity(1L)));

		// act
		AnnouncementDto response = service.getById(1L);

		// assert
		assertNotNull(response);
		verify(repository).findById(anyLong());
	}
	
	@Test
	void shouldDelete() {
		// act
		service.delete(1L);

		// assert
		verify(repository).deleteById(1L);
	}
}
