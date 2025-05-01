package io.github.gms.abstraction;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class AbstractTestContainersIntegrationTest extends AbstractIntegrationTest {

    public static final String DS_DRIVER = "spring.datasource.driver-class-name";
    public static final String DS_URL = "spring.datasource.url";
    public static final String DS_USERNAME = "spring.datasource.username";
    public static final String DS_PASSWORD = "spring.datasource.password";

    public static void initProperties(DynamicPropertyRegistry registry, JdbcDatabaseContainer<?> container) {
        registry.add(DS_DRIVER, container::getDriverClassName);
        registry.add(DS_URL, container::getJdbcUrl);
        registry.add(DS_USERNAME, container::getUsername);
        registry.add(DS_PASSWORD, container::getPassword);
    }
}
