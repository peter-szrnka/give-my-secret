package io.github.gms.functions.iprestriction;

import ch.qos.logback.classic.Logger;
import io.github.gms.abstraction.AbstractLoggingUnitTest;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.model.IpRestrictionPatterns;
import io.github.gms.common.types.GmsException;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.github.gms.util.TestUtils.assertLogContains;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class IpRestrictionServiceImplTest extends AbstractLoggingUnitTest {

    private IpRestrictionRepository repository;
    private IpRestrictionConverter converter;
    private IpRestrictionServiceImpl service;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        repository = mock(IpRestrictionRepository.class);
        converter = mock(IpRestrictionConverter.class);
        service = new IpRestrictionServiceImpl(repository, converter);

        ((Logger) LoggerFactory.getLogger(IpRestrictionServiceImpl.class)).addAppender(logAppender);
    }

    @Test
    void shouldSaveFail() {
        // arrange
        IpRestrictionDto dto = IpRestrictionDto.builder().id(1L).global(false).build();

        // act
        GmsException exception = assertThrows(GmsException.class,
                () -> service.save(dto));

        // assert
        assertNotNull(exception);
        assertEquals("Only global IP restrictions allowed to save with this service!", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("saveInputData")
    void shouldSave(Long id, boolean global) {
        // arrange
        IpRestrictionEntity mockEntity = TestUtils.createIpRestriction();
        when(converter.toEntity(any(IpRestrictionDto.class))).thenReturn(mockEntity);
        when(repository.save(mockEntity)).thenReturn(mockEntity);

        // act
        SaveEntityResponseDto response = service.save(IpRestrictionDto.builder()
                .id(id)
                .global(global)
                .allow(true)
                .build());

        // assert
        assertNotNull(response);
        assertThat(response.getEntityId()).isEqualTo(mockEntity.getId());
        ArgumentCaptor<IpRestrictionDto> dtoArgumentCaptor = ArgumentCaptor.forClass(IpRestrictionDto.class);
        verify(converter).toEntity(dtoArgumentCaptor.capture());
        IpRestrictionDto capturedDto = dtoArgumentCaptor.getValue();
        assertEquals(id, capturedDto.getId());
        assertEquals(global, capturedDto.isGlobal());

        ArgumentCaptor<IpRestrictionEntity> entityArgumentCaptor = ArgumentCaptor.forClass(IpRestrictionEntity.class);
        verify(repository).save(entityArgumentCaptor.capture());
        assertThat(mockEntity.isGlobal()).isTrue();
        assertThat(mockEntity.getSecretId()).isNull();
        assertThat(mockEntity.getUserId()).isNull();
        assertThat(mockEntity.getStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    void shouldFindById() {
        // arrange
        IpRestrictionEntity mockEntity = TestUtils.createIpRestriction();
        mockEntity.setGlobal(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(mockEntity));
        when(converter.toDto(any())).thenReturn(new IpRestrictionDto());

        // act
        IpRestrictionDto response = service.getById(1L);

        // assert
        assertNotNull(response);
        verify(repository).findById(anyLong());
        verify(converter).toDto(any());
    }

    @Test
    void shouldReturnList() {
        // arrange
        Page<IpRestrictionEntity> mockList = new PageImpl<>(Lists.newArrayList(new IpRestrictionEntity()));
        when(repository.findAllGlobal(any(Pageable.class))).thenReturn(mockList);
        when(converter.toDtoList(any(Page.class)))
                .thenReturn(IpRestrictionListDto.builder().resultList(Lists.newArrayList(new IpRestrictionDto())).build());
        Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

        // act
        IpRestrictionListDto response = service.list(pageable);

        // assert
        assertNotNull(response);
        assertEquals(1, response.getResultList().size());
        verify(repository).findAllGlobal(any(Pageable.class));
        verify(converter).toDtoList(any(Page.class));
    }

    @Test
    void shouldNotDeleteUserNonExistingIpRestriction() {
        // arrange
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // act
        GmsException exception = assertThrows(GmsException.class, () -> service.delete(1L));

        // assert
        assertNotNull(exception);
        assertEquals("Entity not found!", exception.getMessage());
        verify(repository).findById(anyLong());
    }

    @Test
    void shouldNotDeleteUserDefinedIpRestriction() {
        // arrange
        IpRestrictionEntity mockEntity = TestUtils.createIpRestriction();
        mockEntity.setGlobal(false);
        when(repository.findById(anyLong())).thenReturn(Optional.of(mockEntity));

        // act
        GmsException exception = assertThrows(GmsException.class, () -> service.delete(1L));

        // assert
        assertNotNull(exception);
        assertEquals("Invalid request, the given resource is not a global IP restriction!", exception.getMessage());
        verify(repository).findById(anyLong());
    }

    @Test
    void shouldDelete() {
        // arrange
        IpRestrictionEntity mockEntity = TestUtils.createIpRestriction();
        mockEntity.setGlobal(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(mockEntity));

        // act
        service.delete(1L);

        // assert
        verify(repository).findById(anyLong());
        verify(repository).delete(mockEntity);
    }

    @Test
    void shouldUpdateIpRestrictionsForSecret() {
        // arrange
        List<IpRestrictionDto> restrictions = List.of(
                IpRestrictionDto.builder().allow(true).ipPattern(".*").build(), // existing
                IpRestrictionDto.builder().id(1L).allow(true).ipPattern("(127.0.0.)[0-9]{1,3}").build()
        );
        List<IpRestrictionEntity> mockEntities = List.of(
                IpRestrictionEntity.builder().id(1L).allow(true).ipPattern("(127.0.0.)[0-9]{1,3}").build(),
                IpRestrictionEntity.builder().id(2L).allow(true).ipPattern("(192.168.0.)[0-9]{1,3}").build()
        );
        when(repository.findAllBySecretId(1L)).thenReturn(mockEntities);
        when(converter.toEntity(any(IpRestrictionDto.class))).thenReturn(TestUtils.createIpRestriction());

        // act
        service.updateIpRestrictionsForSecret(1L, restrictions);

        // assert
        verify(repository).findAllBySecretId(1L);
        verify(converter, times(2)).toEntity(any(IpRestrictionDto.class));
        ArgumentCaptor<Set<Long>> argumentCaptorIds = ArgumentCaptor.forClass(Set.class);
        verify(repository).deleteAllById(argumentCaptorIds.capture());

        assertEquals(1, argumentCaptorIds.getValue().size());
        assertEquals(2L, argumentCaptorIds.getValue().iterator().next());
    }

    @Test
    void shouldReturnAllSecretById() {
        // arrange
        List<IpRestrictionEntity> mockEntities = List.of(
                IpRestrictionEntity.builder().id(1L).allow(true).ipPattern("(127.0.0.)[0-9]{1,3}").build()
        );
        when(repository.findAllBySecretId(1L)).thenReturn(mockEntities);
        when(converter.toDtoList(anyList())).thenReturn(List.of(
                IpRestrictionDto.builder().id(1L).allow(true).ipPattern("(127.0.0.)[0-9]{1,3}").build()
        ));

        // act
        List<IpRestrictionDto> response = service.getAllBySecretId(1L);

        // assert
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(repository).findAllBySecretId(1L);
        verify(converter).toDtoList(anyList());
    }

    @Test
    void shouldCheckIpRestrictionsBySecret() {
        // arrange
        List<IpRestrictionEntity> mockEntities = List.of();
        when(repository.findAllBySecretId(1L)).thenReturn(mockEntities);
        when(converter.toModel(anyList())).thenReturn(new IpRestrictionPatterns(List.of()));

        // act
        IpRestrictionPatterns response = service.checkIpRestrictionsBySecret(1L);

        // assert
        assertNotNull(response);
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
        verify(repository).findAllBySecretId(1L);
        verify(converter).toModel(anyList());
    }

    @Test
    void shouldCheckGlobalIpRestrictions() {
        // arrange
        List<IpRestrictionEntity> mockEntities = List.of();
        when(repository.findAllGlobal()).thenReturn(mockEntities);
        when(converter.toModel(anyList())).thenReturn(new IpRestrictionPatterns(List.of()));

        // act
        IpRestrictionPatterns response = service.checkGlobalIpRestrictions();

        // assert
        assertNotNull(response);
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
        verify(repository).findAllGlobal();
        verify(converter).toModel(anyList());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldToggleStatus(boolean enabled) {
        // arrange
        IpRestrictionEntity mockEntity = TestUtils.createIpRestriction();
        mockEntity.setGlobal(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(mockEntity));

        // act
        service.toggleStatus(1L, enabled);

        // assert
        verify(repository).save(any());
        verify(repository).findById(anyLong());

        ArgumentCaptor<IpRestrictionEntity> argumentCaptor = ArgumentCaptor.forClass(IpRestrictionEntity.class);
        verify(repository).save(argumentCaptor.capture());

        assertEquals(enabled, argumentCaptor.getValue().getStatus() == EntityStatus.ACTIVE);
    }

    @Test
    void shouldDeleteInBatch() {
        // arrange
        Set<Long> userIds = Set.of(1L, 2L);

        // act
        service.batchDeleteByUserIds(userIds);

        // assert
        verify(repository).deleteAllByUserId(userIds);
        assertLogContains(logAppender, "All IP restrictions have been removed for the requested users");
    }

    private static Object[][] saveInputData() {
        return new Object[][]{
                {null, false},
                {1L, true},
                {null, true}
        };
    }
}
