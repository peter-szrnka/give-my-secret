package io.github.gms.functions.maintenance.user;

import io.github.gms.functions.apikey.ApiKeyService;
import io.github.gms.functions.event.EventService;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import io.github.gms.functions.keystore.KeystoreService;
import io.github.gms.functions.message.MessageService;
import io.github.gms.functions.secret.ApiKeyRestrictionRepository;
import io.github.gms.functions.secret.SecretService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAssetDeletionService {

    private final ApiKeyService apiKeyService;
    private final ApiKeyRestrictionRepository apiKeyRestrictionRepository;
    private final KeystoreService keystoreService;
    private final IpRestrictionService ipRestrictionService;
    private final SecretService secretService;
    private final MessageService messageService;
    private final EventService eventService;

    public void executeRequestedUserAssetDeletion(Set<Long> userIds) {
        apiKeyService.batchDeleteByUserIds(userIds);
        apiKeyRestrictionRepository.deleteAllByUserId(userIds);
        keystoreService.batchDeleteByUserIds(userIds);
        ipRestrictionService.batchDeleteByUserIds(userIds);
        secretService.batchDeleteByUserIds(userIds);
        messageService.batchDeleteByUserIds(userIds);
        eventService.batchDeleteByUserIds(userIds);
    }
}
