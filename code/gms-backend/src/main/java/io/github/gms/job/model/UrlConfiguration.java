package io.github.gms.job.model;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.job.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum UrlConfiguration {
    EVENT_MAINTENANCE(UrlConstants.EVENT_MAINTENANCE, EventMaintenanceJob.class),
    GENERATED_KEYSTORE_CLEANUP(UrlConstants.GENERATED_KEYSTORE_CLEANUP, GeneratedKeystoreCleanupJob.class),
    JOB_MAINTENANCE(UrlConstants.JOB_MAINTENANCE, JobMaintenanceJob.class),
    MESSAGE_CLEANUP(UrlConstants.MESSAGE_CLEANUP, MessageCleanupJob.class),
    SECRET_ROTATION(UrlConstants.SECRET_ROTATION, SecretRotationJob.class),
    USER_ANONYMIZATION(UrlConstants.USER_ANONYMIZATION, UserAnonymizationJob.class),
    USER_DELETION(UrlConstants.USER_DELETION, UserDeletionJob.class),
    LDAP_USER_SYNC(UrlConstants.LDAP_USER_SYNC, LdapUserSyncJob.class);

    private final String url;
    private final Class<? extends AbstractJob> clazz;

    public static UrlConfiguration fromUrl(String url) {
        return Arrays.stream(UrlConfiguration.values())
                .filter(urlConfiguration -> urlConfiguration.url.equals(url))
                .findFirst()
                .orElse(null);
    }
}