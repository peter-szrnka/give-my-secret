package io.github.gms.common.abstraction;

import io.github.gms.common.enums.JobStatus;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.common.util.MdcUtils;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import io.github.gms.job.JobEntity;
import io.github.gms.job.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import java.time.Clock;
import java.time.ZonedDateTime;

import static io.github.gms.common.enums.JobStatus.COMPLETED;
import static io.github.gms.common.enums.JobStatus.FAILED;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractJob {

    @Autowired
    protected SystemService systemService;
    @Autowired
    protected SystemPropertyService systemPropertyService;
    @Autowired
    protected Clock clock;
    @Autowired
    protected JobRepository jobRepository;

    public abstract void run();

    protected abstract Pair<SystemProperty, SystemProperty> systemPropertyConfigs();

    @FunctionalInterface
    protected interface BusinessLogicExecutor {
        void execute();
    }

    protected void execute(BusinessLogicExecutor supplierFunction) {
        if (skipJobExecution()) {
            return;
        }

        createJobExecution();

        try {
            supplierFunction.execute();
            completeJobExecution(COMPLETED);
        } catch (Exception e) {
            log.error("Job execution failed", e);
            completeJobExecution(FAILED, e.getMessage());
        }

        MdcUtils.remove(MdcParameter.JOB_ID);
    }

    private void createJobExecution() {
        JobEntity newEntity = jobRepository.save(JobEntity.builder()
                .name(getClass().getSimpleName())
                .status(JobStatus.COMPLETED)
                .creationDate(ZonedDateTime.now(clock))
                .startTime(ZonedDateTime.now(clock))
                .build());

        MdcUtils.putLong(MdcParameter.JOB_ID, newEntity.getId());
    }

    private void completeJobExecution(JobStatus status, String... message) {
        Long executionId = MdcUtils.getLong(MdcParameter.JOB_ID);
        JobEntity entity = jobRepository.findById(executionId).orElseThrow();

        entity.setEndTime(ZonedDateTime.now(clock));
        entity.setDuration(getMillis(entity.getEndTime()) - getMillis(entity.getStartTime()));
        entity.setStatus(status);

        if (message.length > 0) {
            entity.setMessage(message[0]);
        }

        jobRepository.save(entity);
    }

    private boolean skipJobExecution() {
        return jobDisabled() || multiNodeEnabled();
    }

    private boolean jobDisabled() {
        return !systemPropertyService.getBoolean(systemPropertyConfigs().getFirst());
    }

    private boolean multiNodeEnabled() {
        return systemPropertyService.getBoolean(SystemProperty.ENABLE_MULTI_NODE) &&
                !StringUtils.equals(systemPropertyService.get(systemPropertyConfigs().getSecond()), systemService.getContainerId());
    }

    private static long getMillis(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli();
    }
}
