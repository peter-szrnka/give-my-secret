package io.github.gms.functions.iprestriction;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.github.gms.util.LogAssertionUtils.assertLogContains;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

        addAppender(IpRestrictionServiceImpl.class);
    }

    @Test
    void save_whenEntityIsNotGlobal_thenThrowGmsException() {
        // given
        IpRestrictionDto dto = IpRestrictionDto.builder().id(1L).global(false).build();

        // when
        GmsException exception = assertThrows(GmsException.class,
                () -> service.save(dto));

        // then
        assertNotNull(exception);
        assertEquals("Only global IP restrictions allowed to save with this service!", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("saveInputData")
    void save_whenEntityIsCorrect_thenReturnOk(Long id, boolean global) {
        // given
        IpRestrictionEntity mockEntity = TestUtils.createIpRestriction();
        when(converter.toEntity(any(IpRestrictionDto.class))).thenReturn(mockEntity);
        when(repository.save(mockEntity)).thenReturn(mockEntity);

        // when
        SaveEntityResponseDto response = service.save(IpRestrictionDto.builder()
                .id(id)
                .global(global)
                .allow(true)
                .build());

        // then
        assertNotNull(response);
        assertThat(response.getEntityId()).isEqualTo(mockEntity.getId());
        ArgumentCaptor<IpRestrictionDto> dtoArgumentCaptor = ArgumentCaptor.forClass(IpRestrictionDto.class);
        verify(converter).toEntity(dtoArgumentCaptor.capture());
        IpRestrictionDto capturedDto = dtoArgumentCaptor.getValue();
        assertEquals(id, capturedDto.getId());
        assertEquals(global, capturedDto.isGlobal());

        ArgumentCaptor<IpRestrictionEntity> entityArgumentCaptor = ArgumentCaptor.forClass(IpRestrictionEntity.class);
        verify(repository).save(entityArgumentCaptor.capture());
        assertThat(entityArgumentCaptor.getValue().isGlobal()).isTrue();
        assertThat(entityArgumentCaptor.getValue().getSecretId()).isNull();
        assertThat(entityArgumentCaptor.getValue().getUserId()).isNull();
        assertThat(entityArgumentCaptor.getValue().getStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    void getById_whenEntityExists_thenReturnEntity() {
        // given
        IpRestrictionEntity mockEntity = TestUtils.createIpRestriction();
        mockEntity.setGlobal(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(mockEntity));
        when(converter.toDto(any())).thenReturn(new IpRestrictionDto());

        // when
        IpRestrictionDto response = service.getById(1L);

        // then
        assertNotNull(response);
        verify(repository).findById(anyLong());
        verify(converter).toDto(any());
    }

    @Test
    void list_whenInputProvided_thenReturnOk() {
        // given
        Page<IpRestrictionEntity> mockList = new PageImpl<>(Lists.newArrayList(new IpRestrictionEntity()));
        when(repository.findAllGlobal(any(Pageable.class))).thenReturn(mockList);
        when(converter.toDtoList(any(Page.class)))
                .thenReturn(IpRestrictionListDto.builder().resultList(Lists.newArrayList(new IpRestrictionDto())).build());
        Pageable pageable = ConverterUtils.createPageable("ASC", "id", 0, 10);

        // when
        IpRestrictionListDto response = service.list(pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.getResultList().size());
        verify(repository).findAllGlobal(any(Pageable.class));
        verify(converter).toDtoList(any(Page.class));
    }

    @Test
    void delete_whenUserDoesNotExist_thenThrowGmsException() {
        // given
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        GmsException exception = assertThrows(GmsException.class, () -> service.delete(1L));

        // then
        assertNotNull(exception);
        assertEquals("Entity not found!", exception.getMessage());
        verify(repository).findById(anyLong());
    }

    @Test
    void delete_whenInputIsNotGlobalIpRestriction_thenThrowGmsException() {
        // given
        IpRestrictionEntity mockEntity = TestUtils.createIpRestriction();
        mockEntity.setGlobal(false);
        when(repository.findById(anyLong())).thenReturn(Optional.of(mockEntity));

        // when
        GmsException exception = assertThrows(GmsException.class, () -> service.delete(1L));

        // then
        assertNotNull(exception);
        assertEquals("Invalid request, the given resource is not a global IP restriction!", exception.getMessage());
        verify(repository).findById(anyLong());
    }

    @Test
    void delete_whenInputIsCorrect_thenRemove() {
        // given
        IpRestrictionEntity mockEntity = TestUtils.createIpRestriction();
        mockEntity.setGlobal(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(mockEntity));

        // when
        service.delete(1L);

        // then
        verify(repository).findById(anyLong());
        verify(repository).delete(mockEntity);
    }

    @Test
    void updateIpRestrictionsForSecret_whenProperInputProvided_thenUpdateData() {
        // given
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

        // when
        service.updateIpRestrictionsForSecret(1L, restrictions);

        // then
        verify(repository).findAllBySecretId(1L);
        ArgumentCaptor<IpRestrictionDto> argumentCaptor = ArgumentCaptor.forClass(IpRestrictionDto.class);
        verify(converter, times(2)).toEntity(argumentCaptor.capture());
        List<IpRestrictionDto> capturedDtos = argumentCaptor.getAllValues();
        capturedDtos.forEach(dto -> assertEquals(1L, dto.getSecretId()));

        ArgumentCaptor<Set<Long>> argumentCaptorIds = ArgumentCaptor.forClass(Set.class);
        verify(repository).deleteAllById(argumentCaptorIds.capture());

        assertEquals(1, argumentCaptorIds.getValue().size());
        assertEquals(2L, argumentCaptorIds.getValue().iterator().next());
    }

    @Test
    void getAllBySecretId_whenInputProvided_thenReturnData() {
        // given
        List<IpRestrictionEntity> mockEntities = List.of(
                IpRestrictionEntity.builder().id(1L).allow(true).ipPattern("(127.0.0.)[0-9]{1,3}").build()
        );
        when(repository.findAllBySecretId(1L)).thenReturn(mockEntities);
        when(converter.toDtoList(anyList())).thenReturn(List.of(
                IpRestrictionDto.builder().id(1L).allow(true).ipPattern("(127.0.0.)[0-9]{1,3}").build()
        ));

        // when
        List<IpRestrictionDto> response = service.getAllBySecretId(1L);

        // then
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(repository).findAllBySecretId(1L);
        verify(converter).toDtoList(anyList());
    }

    @Test
    void checkIpRestrictionsBySecret_whenInputProvided_thenReturnOk() {
        // given
        List<IpRestrictionEntity> mockEntities = List.of();
        when(repository.findAllBySecretId(1L)).thenReturn(mockEntities);
        when(converter.toModel(anyList())).thenReturn(new IpRestrictionPatterns(List.of()));

        // when
        IpRestrictionPatterns response = service.checkIpRestrictionsBySecret(1L);

        // then
        assertNotNull(response);
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
        verify(repository).findAllBySecretId(1L);
        verify(converter).toModel(anyList());
    }

    @Test
    void checkGlobalIpRestrictions_whenInputProvided_thenReturnOk() {
        // given
        List<IpRestrictionEntity> mockEntities = List.of();
        when(repository.findAllGlobal()).thenReturn(mockEntities);
        when(converter.toModel(anyList())).thenReturn(new IpRestrictionPatterns(List.of()));

        // when
        IpRestrictionPatterns response = service.checkGlobalIpRestrictions();

        // then
        assertNotNull(response);
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
        verify(repository).findAllGlobal();
        verify(converter).toModel(anyList());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void toggleStatus_whenInputProvided_thenReturnOk(boolean enabled) {
        // given
        IpRestrictionEntity mockEntity = TestUtils.createIpRestriction();
        mockEntity.setGlobal(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(mockEntity));

        // when
        service.toggleStatus(1L, enabled);

        // then
        verify(repository).save(any());
        verify(repository).findById(anyLong());

        ArgumentCaptor<IpRestrictionEntity> argumentCaptor = ArgumentCaptor.forClass(IpRestrictionEntity.class);
        verify(repository).save(argumentCaptor.capture());

        assertEquals(enabled, argumentCaptor.getValue().getStatus() == EntityStatus.ACTIVE);
    }

    @Test
    void batchDeleteByUserIds_whenInputProvided_thenReturnOk() {
        // given
        Set<Long> userIds = Set.of(1L, 2L);

        // when
        service.batchDeleteByUserIds(userIds);

        // then
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

