package io.github.gms.functions.maintenance;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.functions.apikey.ApiKeyService;
import io.github.gms.functions.event.EventService;
import io.github.gms.functions.iprestriction.IpRestrictionService;
import io.github.gms.functions.keystore.KeystoreService;
import io.github.gms.functions.maintenance.user.UserAssetDeletionService;
import io.github.gms.functions.message.MessageService;
import io.github.gms.functions.secret.ApiKeyRestrictionRepository;
import io.github.gms.functions.secret.SecretService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class UserAssetDeletionServiceTest extends AbstractUnitTest {

    private ApiKeyService apiKeyService;
    private ApiKeyRestrictionRepository apiKeyRestrictionRepository;
    private KeystoreService keystoreService;
    private IpRestrictionService ipRestrictionService;
    private SecretService secretService;
    private MessageService messageService;
    private EventService eventService;
    private UserAssetDeletionService service;

    @BeforeEach
    void setup() {
        apiKeyService = mock(ApiKeyService.class);
        apiKeyRestrictionRepository = mock(ApiKeyRestrictionRepository.class);
        keystoreService = mock(KeystoreService.class);
        ipRestrictionService = mock(IpRestrictionService.class);
        secretService = mock(SecretService.class);
        messageService = mock(MessageService.class);
        eventService = mock(EventService.class);
        service = new UserAssetDeletionService(
                apiKeyService,
                apiKeyRestrictionRepository,
                keystoreService,
                ipRestrictionService,
                secretService,
                messageService,
                eventService);
    }

    @Test
    void executeRequestedUserAssetDeletion_whenCalled_thenExcute() {
        // arrange
        Set<Long> mockUserIds = Set.of(1L, 2L);

        // act
        service.executeRequestedUserAssetDeletion(mockUserIds);

        // assert
        verify(apiKeyService).batchDeleteByUserIds(mockUserIds);
        verify(apiKeyRestrictionRepository).deleteAllByUserId(mockUserIds);
        verify(keystoreService).batchDeleteByUserIds(mockUserIds);
        verify(ipRestrictionService).batchDeleteByUserIds(mockUserIds);
        verify(secretService).batchDeleteByUserIds(mockUserIds);
        verify(messageService).batchDeleteByUserIds(mockUserIds);
        verify(eventService).batchDeleteByUserIds(mockUserIds);
    }
}
