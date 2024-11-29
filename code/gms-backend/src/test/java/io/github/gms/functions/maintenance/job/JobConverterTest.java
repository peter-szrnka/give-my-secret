package io.github.gms.functions.maintenance.job;

import com.google.common.collect.Lists;
import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.enums.JobStatus;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class JobConverterTest extends AbstractUnitTest {
    
    private final JobConverter converter = new JobConverter();

    @Test
    void toDtoList_whenValidInputProvided_thenReturnResultList() {
        // arrange
        JobEntity apiKeyEntity = TestUtils.createJobEntity();
        Page<JobEntity> entityList = new PageImpl<>(Lists.newArrayList(apiKeyEntity));

        // act
        JobListDto resultList = converter.toDtoList(entityList);

        // assert
        assertNotNull(resultList);
        assertEquals(1, resultList.getResultList().size());
        assertEquals(1L, resultList.getTotalElements());

        JobDto entity = resultList.getResultList().getFirst();
        assertEquals(1L, entity.getId());
        assertEquals("job", entity.getName());
        assertEquals(JobStatus.COMPLETED, entity.getStatus());
        assertEquals(100L, entity.getDuration());
        assertEquals("test", entity.getMessage());
    }
}
