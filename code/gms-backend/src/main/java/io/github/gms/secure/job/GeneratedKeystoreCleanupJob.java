package io.github.gms.secure.job;

import io.github.gms.common.abstraction.AbstractLimitBasedJob;
import io.github.gms.secure.service.KeystoreFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "config.job.generatedKeystoreCleanup.enabled", havingValue = "true", matchIfMissing = true)
public class GeneratedKeystoreCleanupJob extends AbstractLimitBasedJob {

    @Autowired
    private KeystoreFileService service;

    @Scheduled(cron = "45 * * * * ?")
    public void execute() {
        long deletedCount = service.deleteTempKeystoreFiles();

        if (deletedCount > 0) {
            log.info("{} temporary keystore(s) deleted", deletedCount);
        }
    }
}