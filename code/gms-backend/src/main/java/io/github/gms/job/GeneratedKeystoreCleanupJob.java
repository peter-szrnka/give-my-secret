package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.functions.keystore.KeystoreFileService;
import io.github.gms.functions.systemproperty.SystemPropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static io.github.gms.common.util.Constants.TRUE;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "config.job.generatedKeystoreCleanup.enabled", havingValue = TRUE, matchIfMissing = true)
public class GeneratedKeystoreCleanupJob extends AbstractJob {

    private final KeystoreFileService service;

    public GeneratedKeystoreCleanupJob(Environment environment, SystemPropertyService systemPropertyService, KeystoreFileService service) {
        super(environment, systemPropertyService);
        this.service = service;
    }

    @Scheduled(cron = "0 45 * * * ?")
    public void execute() {
        long deletedCount = service.deleteTempKeystoreFiles();

        if (deletedCount > 0) {
            log.info("{} temporary keystore(s) deleted", deletedCount);
        }
    }
}