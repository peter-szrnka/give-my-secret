package io.github.gms.functions.iprestriction;

import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.model.IpRestrictionPattern;
import io.github.gms.common.util.MdcUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class IpRestrictionConverterImplTest extends AbstractUnitTest {

    private Clock clock;
    private IpRestrictionConverterImpl converter;

    @BeforeEach
    void beforeEach() {
        // init
        clock = mock(Clock.class);
        converter = new IpRestrictionConverterImpl(clock);
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
    void checkToModelList() {
        // arrange
        when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        IpRestrictionEntity ipRestrictionEntity = TestUtils.createIpRestriction();
        ipRestrictionEntity.setCreationDate(ZonedDateTime.now(clock));
        List<IpRestrictionEntity> entityList = Lists.newArrayList(ipRestrictionEntity);

        // act
        List<IpRestrictionPattern> resultList = converter.toModelList(entityList);

        // assert
        assertNotNull(resultList);
        IpRestrictionPattern dto = resultList.getFirst();
        assertEquals(".*", dto.getIpPattern());
        verify(clock).instant();
        verify(clock).getZone();
    }

    @ParameterizedTest
    @MethodSource("testData")
    void checkToEntity(Long id, int expectedTimes) {
        try (MockedStatic<MdcUtils> utilsMockedStatic = mockStatic(MdcUtils.class)) {
            // arrange
            when(clock.instant()).thenReturn(Instant.parse("2023-06-29T00:00:00Z"));
            when(clock.getZone()).thenReturn(ZoneOffset.UTC);
            utilsMockedStatic.when(MdcUtils::getUserId).thenReturn(1L);
            IpRestrictionDto dto = TestUtils.createIpRestrictionDto();
            dto.setId(id);

            // act
            IpRestrictionEntity response = converter.toEntity(dto);

            // assert
            assertNotNull(response);
            verify(clock, times(expectedTimes)).instant();
            verify(clock, times(expectedTimes)).getZone();
            utilsMockedStatic.verify(MdcUtils::getUserId);
        }
    }

    private static Object[] testData() {
        return new Object[][] {
                { null, 2 },
                { 1L, 1 }
        };
    }
}
