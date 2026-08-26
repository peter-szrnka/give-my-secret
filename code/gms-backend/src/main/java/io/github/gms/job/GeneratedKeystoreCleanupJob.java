package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.keystore.KeystoreFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeneratedKeystoreCleanupJob extends AbstractJob {

    private final KeystoreFileService service;

    @Override
    @SchedulerLock(name = "generatedKeystoreCleanupJob",
            lockAtLeastFor = "${config.job.generatedKeystoreCleanupJob.lockAtLeastFor}",
            lockAtMostFor = "${config.job.generatedKeystoreCleanupJob.lockAtMostFor}")
    @Scheduled(cron = "0 45 * * * ?")
    public void run() {
        execute(this::businessLogic);
    }

    @Override
    protected SystemProperty enableConfig() {
        return SystemProperty.KEYSTORE_CLEANUP_JOB_ENABLED;
    }

    private void businessLogic() {
        long deletedCount = service.deleteTempKeystoreFiles();

        if (deletedCount > 0) {
            log.info("{} temporary keystore(s) deleted", deletedCount);
        }
    }
}