package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.maintenance.job.JobEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import static io.github.gms.common.enums.SystemProperty.JOB_MAINTENANCE_JOB_ENABLED;

@Slf4j
@Component
public class JobMaintenanceJob extends AbstractLimitBasedJob {

    @Override
    @SchedulerLock(name = "jobMaintenanceJob",
            lockAtLeastFor = "${config.job.jobMaintenanceJob.lockAtLeastFor}",
            lockAtMostFor = "${config.job.jobMaintenanceJob.lockAtMostFor}")
    @Scheduled(cron = "0 30 * * * *")
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
    protected SystemProperty enableConfig() {
        return JOB_MAINTENANCE_JOB_ENABLED;
    }
}
