package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.enums.SystemProperty;
import io.github.gms.functions.keystore.KeystoreFileService;
import io.github.gms.functions.system.SystemService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
public class GeneratedKeystoreCleanupJob extends AbstractJob {

    private final KeystoreFileService service;

    public GeneratedKeystoreCleanupJob(SystemService systemService, SystemPropertyService systemPropertyService, KeystoreFileService service) {
        super(systemService, systemPropertyService, SystemProperty.KEYSTORE_CLEANUP_JOB_ENABLED);
        this.service = service;
    }

    @Scheduled(cron = "0 45 * * * ?")
    public void execute() {
        if (skipJobExecution(SystemProperty.KEYSTORE_CLEANUP_RUNNER_CONTAINER_ID)) {
            return;
        }

        long deletedCount = service.deleteTempKeystoreFiles();

        if (deletedCount > 0) {
            log.info("{} temporary keystore(s) deleted", deletedCount);
        }
    }
}