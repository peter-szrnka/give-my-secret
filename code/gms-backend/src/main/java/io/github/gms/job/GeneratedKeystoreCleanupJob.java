package io.github.gms.job;

import io.github.gms.functions.keystore.KeystoreFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "config.job.generatedKeystoreCleanup.enabled", havingValue = "true", matchIfMissing = true)
public class GeneratedKeystoreCleanupJob {

    private final KeystoreFileService service;

    @Scheduled(cron = "0 45 * * * ?")
    public void execute() {
        long deletedCount = service.deleteTempKeystoreFiles();

        if (deletedCount > 0) {
            log.info("{} temporary keystore(s) deleted", deletedCount);
        }
    }
}