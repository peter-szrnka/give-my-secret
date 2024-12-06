package io.github.gms.job;

import io.github.gms.common.abstraction.AbstractJob;
import io.github.gms.common.abstraction.GmsController;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.types.AuditTarget;
import io.github.gms.common.util.MdcUtils;
import io.github.gms.job.model.UrlConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.ROLE_ADMIN;
import static io.github.gms.common.util.Constants.TRUE;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize(ROLE_ADMIN)
@AuditTarget(EventTarget.ANNOUNCEMENT)
@RequestMapping("/secure/job_execution")
public class ManualJobExecutionController implements GmsController {

    private final ApplicationContext applicationContext;

    @GetMapping("/{jobName}")
    public ResponseEntity<Void> runJobByName(@PathVariable("jobName") String jobName) {
        UrlConfiguration urlConfiguration = UrlConfiguration.fromUrl(jobName);

        if (urlConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        return runJob(urlConfiguration.getClazz());
    }

    private <T extends AbstractJob> ResponseEntity<Void> runJob(@NonNull Class<T> clazz) {
        MdcUtils.put(MdcParameter.MANUAL_JOB_EXECUTION, TRUE);

        try {
            T job = applicationContext.getBean(clazz);
            job.run();

            MdcUtils.remove(MdcParameter.MANUAL_JOB_EXECUTION);
            return ResponseEntity.ok().build();
        } catch (NoSuchBeanDefinitionException e) {
            MdcUtils.remove(MdcParameter.MANUAL_JOB_EXECUTION);
            return ResponseEntity.notFound().build();
        }
    }
}
