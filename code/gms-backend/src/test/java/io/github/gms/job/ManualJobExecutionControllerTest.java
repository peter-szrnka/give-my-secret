package io.github.gms.job;

import io.github.gms.abstraction.AbstractUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

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
        manualJobExecutionController = new ManualJobExecutionController(eventMaintenanceJob, generatedKeystoreCleanupJob, jobMaintenanceJob, messageCleanupJob, secretRotationJob, userAnonymizationJob, userDeletionJob);
    }

    @Test
    void testEventMaintenance() {
        ResponseEntity<Void> response = manualJobExecutionController.eventMaintenance();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(eventMaintenanceJob, times(1)).run();
    }

    @Test
    void testGeneratedKeystoreCleanup() {
        ResponseEntity<Void> response = manualJobExecutionController.generatedKeystoreCleanup();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(generatedKeystoreCleanupJob, times(1)).run();
    }

    @Test
    void testJobMaintenance() {
        ResponseEntity<Void> response = manualJobExecutionController.jobMaintenance();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(jobMaintenanceJob, times(1)).run();
    }

    @Test
    void testMessageCleanup() {
        ResponseEntity<Void> response = manualJobExecutionController.messageCleanup();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(messageCleanupJob, times(1)).run();
    }

    @Test
    void testSecretRotation() {
        ResponseEntity<Void> response = manualJobExecutionController.secretRotation();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(secretRotationJob, times(1)).run();
    }

    @Test
    void testUserAnonymization() {
        ResponseEntity<Void> response = manualJobExecutionController.userAnonymization();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(userAnonymizationJob, times(1)).run();
    }

    @Test
    void testUserDeletion() {
        ResponseEntity<Void> response = manualJobExecutionController.userDeletion();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(userDeletionJob, times(1)).run();
    }

    @Test
    void testLdapUserSync() {
        ReflectionTestUtils.setField(manualJobExecutionController, "ldapUserSyncJob", ldapUserSyncJob);

        ResponseEntity<Void> response = manualJobExecutionController.ldapUserSync();

        // assert
        assertEquals(ResponseEntity.ok().build(), response);
        verify(ldapUserSyncJob, times(1)).run();
    }

    @Test
    void testLdapUserSyncNotFound() {
        ReflectionTestUtils.setField(manualJobExecutionController, "ldapUserSyncJob", null);

        ResponseEntity<Void> response = manualJobExecutionController.ldapUserSync();

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(ldapUserSyncJob, never()).run();
    }
}