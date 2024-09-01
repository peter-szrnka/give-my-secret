package io.github.gms.functions.iprestriction;

import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.common.model.IpRestrictionPatterns;
import io.github.gms.common.util.MdcUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class IpRestrictionConverterTest extends AbstractUnitTest {

    private Clock clock;
    private IpRestrictionConverter converter;

    @BeforeEach
    void beforeEach() {
        // init
        clock = mock(Clock.class);
        converter = new IpRestrictionConverter(clock);
    }

    @Test
    void checkToList() {
        // arrange
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        IpRestrictionEntity ipRestrictionEntity = TestUtils.createIpRestriction();
        ipRestrictionEntity.setCreationDate(ZonedDateTime.now(clock));
        List<IpRestrictionEntity> entityList = Lists.newArrayList(ipRestrictionEntity);

        // act
        List<IpRestrictionDto> resultList = converter.toDtoList(entityList);

        // assert
        assertNotNull(resultList);
        IpRestrictionDto entity = resultList.getFirst();
        assertEquals(1L, entity.getId());
        assertEquals("2023-06-29T00:00Z", entity.getCreationDate().toString());
        verify(clock).instant();
        verify(clock).getZone();
    }

    @Test
    void checkToDto() {
        // arrange
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        IpRestrictionEntity ipRestrictionEntity = TestUtils.createIpRestriction();
        ipRestrictionEntity.setCreationDate(ZonedDateTime.now(clock));

        // act
        IpRestrictionDto response = converter.toDto(ipRestrictionEntity);

        // assert
        assertEquals(EntityStatus.ACTIVE, response.getStatus());
        assertEquals("IpRestrictionDto(id=1, secretId=1, ipPattern=.*, allow=true, global=false, status=ACTIVE, creationDate=2023-06-29T00:00Z, lastModified=null)", response.toString());
    }

    @Test
    void checkToModelList() {
        // arrange
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        IpRestrictionEntity ipRestrictionEntity = TestUtils.createIpRestriction();
        ipRestrictionEntity.setCreationDate(ZonedDateTime.now(clock));
        List<IpRestrictionEntity> entityList = Lists.newArrayList(ipRestrictionEntity);

        // act
        IpRestrictionPatterns resultList = converter.toModel(entityList);

        // assert
        assertNotNull(resultList);
        assertNotNull(resultList.getItems());
        assertFalse(resultList.getItems().isEmpty());
        IpRestrictionPattern dto = resultList.getItems().getFirst();
        assertEquals(".*", dto.getIpPattern());
        verify(clock).instant();
        verify(clock).getZone();
    }

    @ParameterizedTest
    @MethodSource("testData")
    void checkToEntity(Long id, int expectedTimes, String expectedResponse) {
        try (MockedStatic<MdcUtils> utilsMockedStatic = mockStatic(MdcUtils.class)) {
            // arrange
            when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
            when(clock.getZone()).thenReturn(ZoneOffset.UTC);
            utilsMockedStatic.when(MdcUtils::getUserId).thenReturn(1L);
            IpRestrictionDto dto = TestUtils.createIpRestrictionDto();
            dto.setId(id);
            dto.setGlobal(true);
            dto.setStatus(EntityStatus.ACTIVE);
            dto.setCreationDate(ZonedDateTime.now(clock));

            // act
            IpRestrictionEntity response = converter.toEntity(dto);

            // assert
            assertNotNull(response);
            assertNotNull(response.getCreationDate());
            assertThat(response.isGlobal()).isTrue();
            assertThat(response.getStatus()).isEqualTo(EntityStatus.ACTIVE);
            assertThat(response).hasToString(expectedResponse);
            verify(clock, times(expectedTimes)).instant();
            verify(clock, times(expectedTimes)).getZone();
            utilsMockedStatic.verify(MdcUtils::getUserId);
        }
    }

    @Test
    void checkToListDto() {
        // arrange
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        IpRestrictionEntity ipRestrictionEntity = TestUtils.createIpRestriction();
        ipRestrictionEntity.setCreationDate(ZonedDateTime.now(clock));
        Page<IpRestrictionEntity> entityList = new PageImpl<>(List.of(ipRestrictionEntity));

        // act
        IpRestrictionListDto resultList = converter.toDtoList(entityList);

        // assert
        assertNotNull(resultList);
        assertEquals(1, resultList.getTotalElements());
        IpRestrictionDto entity = resultList.getResultList().getFirst();
        assertEquals(1L, entity.getId());
        assertEquals("2023-06-29T00:00Z", entity.getCreationDate().toString());
        verify(clock).instant();
        verify(clock).getZone();
    }

    private static Object[] testData() {
        return new Object[][] {
                { null, 3, "IpRestrictionEntity(id=null, userId=1, secretId=1, status=ACTIVE, ipPattern=.*, allow=true, global=true, creationDate=2023-06-29T00:00Z, lastModified=2023-06-29T00:00Z)" },
                { 1L, 2, "IpRestrictionEntity(id=1, userId=1, secretId=1, status=ACTIVE, ipPattern=.*, allow=true, global=true, creationDate=2023-06-29T00:00Z, lastModified=2023-06-29T00:00Z)" }
        };
    }
}
