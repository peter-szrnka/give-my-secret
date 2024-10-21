package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.common.enums.SystemProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.github.gms.common.enums.SystemProperty.JOB_MAINTENANCE_JOB_ENABLED;
import static io.github.gms.common.enums.SystemProperty.JOB_MAINTENANCE_RUNNER_CONTAINER_ID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobMaintenanceJob extends AbstractLimitBasedJob {

    private final JobRepository jobRepository;

    @Override
    @Scheduled(cron = "30 * * * * *")
    public void run() {
        if (skipJobExecution()) {
            return;
        }

        List<JobEntity> jobs = jobRepository.findAllOld(processConfig(SystemProperty.OLD_JOB_ENTRY_LIMIT));

        if (jobs.isEmpty()) {
            return;
        }

        jobRepository.deleteAll(jobs);
        log.info("{} old job log(s) deleted", jobs.size());
    }

    @Override
    protected Pair<SystemProperty, SystemProperty> systemPropertyConfigs() {
        return Pair.of(JOB_MAINTENANCE_JOB_ENABLED, JOB_MAINTENANCE_RUNNER_CONTAINER_ID);
    }
}
