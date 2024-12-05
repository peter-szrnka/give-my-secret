package io.github.gms.job;

import io.github.gms.abstraction.AbstractUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class ManualJobExecutionControllerTest extends AbstractUnitTest {

    private EventMaintenanceJob eventMaintenanceJob;
    private GeneratedKeystoreCleanupJob generatedKeystoreCleanupJob;
    private JobMaintenanceJob jobMaintenanceJob;
    private MessageCleanupJob messageCleanupJob;
    private SecretRotationJob secretRotationJob;
    private UserAnonymizationJob userAnonymizationJob;
    private UserDeletionJob userDeletionJob;
    private LdapUserSyncJob ldapUserSyncJob;
    private ManualJobExecutionController manualJobExecutionController;

    @BeforeEach
    void setUp() {
        eventMaintenanceJob = mock(EventMaintenanceJob.class);
        generatedKeystoreCleanupJob = mock(GeneratedKeystoreCleanupJob.class);
        jobMaintenanceJob = mock(JobMaintenanceJob.class);
        messageCleanupJob = mock(MessageCleanupJob.class);
        secretRotationJob = mock(SecretRotationJob.class);
        userAnonymizationJob = mock(UserAnonymizationJob.class);
        userDeletionJob = mock(UserDeletionJob.class);
        ldapUserSyncJob = mock(LdapUserSyncJob.class);
        manualJobExecutionController = new ManualJobExecutionController(eventMaintenanceJob, generatedKeystoreCleanupJob, jobMaintenanceJob, messageCleanupJob, secretRotationJob, userAnonymizationJob, userDeletionJob, null);
    }

    @Test
    void eventMaintenance_whenCalled_thenRunEventMaintenance() {
        ResponseEntity<Void> response = manualJobExecutionController.eventMaintenance();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(eventMaintenanceJob).run();
    }

    @Test
    void generatedKeystoreCleanup_whenCalled_thenCleanupGeneratedKeystores() {
        ResponseEntity<Void> response = manualJobExecutionController.generatedKeystoreCleanup();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(generatedKeystoreCleanupJob).run();
    }

    @Test
    void jobMaintenance_whenCalled_thenRunJobMaintenance() {
        ResponseEntity<Void> response = manualJobExecutionController.jobMaintenance();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(jobMaintenanceJob).run();
    }

    @Test
    void messageCleanup_whenCalled_thenCleanupMessages() {
        ResponseEntity<Void> response = manualJobExecutionController.messageCleanup();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(messageCleanupJob).run();
    }

    @Test
    void secretRotation_whenCalled_thenRotateSecrets() {
        ResponseEntity<Void> response = manualJobExecutionController.secretRotation();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(secretRotationJob).run();
    }

    @Test
    void userAnonymization_whenCalled_thenAnonymizeUsers() {
        ResponseEntity<Void> response = manualJobExecutionController.userAnonymization();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(userAnonymizationJob).run();
    }

    @Test
    void userDeletion_whenCalled_thenDeleteUsers() {
        ResponseEntity<Void> response = manualJobExecutionController.userDeletion();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(userDeletionJob).run();
    }

    @Test
    void ldapUserSync_whenAuthModeIsLdap_thenSyncLdapUsers() {
        manualJobExecutionController = new ManualJobExecutionController(eventMaintenanceJob, generatedKeystoreCleanupJob, jobMaintenanceJob, messageCleanupJob, secretRotationJob, userAnonymizationJob, userDeletionJob, ldapUserSyncJob);

        ResponseEntity<Void> response = manualJobExecutionController.ldapUserSync();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(ldapUserSyncJob).run();
    }

    @Test
    void ldapUserSync_whenAuthModeIsDb_thenSkipSyncLdapUsers() {
        ResponseEntity<Void> response = manualJobExecutionController.ldapUserSync();

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(ldapUserSyncJob, never()).run();
    }
}