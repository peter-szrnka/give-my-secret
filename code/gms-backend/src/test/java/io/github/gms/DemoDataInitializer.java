package io.github.gms;

import io.github.gms.util.DemoDataManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DemoDataInitializer {

    @Autowired
    private DemoDataManagerService demoDataManagerService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Let's add some test data");
        demoDataManagerService.initTestData();
    }
}
