package io.github.gms.functions.maintenance.job;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.common.util.ConverterUtils;
import io.github.gms.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link JobMaintenanceController}
 *
 * @author Peter Szrnka
 */
class JobMaintenanceControllerTest extends AbstractUnitTest {

    private JobMaintenanceService service;
    private JobMaintenanceController controller;

    @BeforeEach
    void setupTest() {
        service = mock(JobMaintenanceService.class);
        controller = new JobMaintenanceController(service);
    }

    @Test
    void list_whenValidInputProvided_thenReturnResultList() {
        // arrange
        JobListDto dtoList = TestUtils.createJobListDto();
        Pageable pageable = ConverterUtils.createPageable("DESC", "id", 0, 10);
        when(service.list(pageable)).thenReturn(dtoList);

        // act
        JobListDto response = controller.list(
                "DESC",
                "id",
                0,
                10
        );

        // assert
        assertNotNull(response);
        assertEquals(dtoList, response);
        verify(service).list(pageable);
    }
}
