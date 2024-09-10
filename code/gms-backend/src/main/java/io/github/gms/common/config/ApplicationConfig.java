package io.github.gms.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import io.github.gms.common.logging.GmsJacksonAnnotationIntrospector;
import io.github.gms.common.interceptor.HttpClientResponseLoggingInterceptor;
import io.github.gms.functions.announcement.AnnouncementRepository;
import io.github.gms.functions.apikey.ApiKeyRepository;
import io.github.gms.functions.event.EventRepository;
import io.github.gms.functions.iprestriction.IpRestrictionRepository;
import io.github.gms.functions.keystore.KeystoreAliasRepository;
import io.github.gms.functions.keystore.KeystoreRepository;
import io.github.gms.functions.message.MessageRepository;
import io.github.gms.functions.secret.ApiKeyRestrictionRepository;
import io.github.gms.functions.secret.SecretRepository;
import io.github.gms.functions.systemproperty.SystemPropertyRepository;
import io.github.gms.functions.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.time.Clock;

import static io.github.gms.common.util.Constants.LOGGING_OBJECT_MAPPER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Configuration
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = "io.github.gms")
@EnableJpaRepositories(basePackageClasses = {
		AnnouncementRepository.class,
		ApiKeyRepository.class,
		ApiKeyRestrictionRepository.class,
		EventRepository.class,
		IpRestrictionRepository.class,
		KeystoreAliasRepository.class,
		KeystoreRepository.class,
		MessageRepository.class,
		SecretRepository.class,
		SystemPropertyRepository.class,
		UserRepository.class

})
public class ApplicationConfig implements WebMvcConfigurer {

	@Value("${config.resource-handler.disabled}")
	private boolean resourceHandlerDisabled;

	@Bean("secretRotationExecutor")
    public TaskExecutor getAsyncExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("secret-rotation-");
        return executor;
    }
	
	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		return baseObjectMapper();
	}

	@Bean(LOGGING_OBJECT_MAPPER)
	public ObjectMapper loggingObjectMapper() {
		return baseObjectMapper()
				.enable(SerializationFeature.INDENT_OUTPUT)
				.setAnnotationIntrospector(new GmsJacksonAnnotationIntrospector());
	}

	@Bean
	public RestTemplate restTemplate(@Value("${config.logging.httpClient.enabled}") boolean httpClientLoggingEnabled) {
		return new RestTemplateBuilder()
				.requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
				.additionalInterceptors(new HttpClientResponseLoggingInterceptor(httpClientLoggingEnabled))
				.build();
	}
	
	@Bean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}
	
	@Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
		if (resourceHandlerDisabled) {
			log.info("Resource handler is disabled.");
			return;
		}

		registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(@NonNull String resourcePath, @NonNull Resource location) throws IOException {
						Resource requestedResource = location.createRelative(resourcePath);
                        return checkRequestedResource(requestedResource) ? requestedResource : new ClassPathResource("/static/index.html");
                    }
                });
    }

	@Bean
	public CodeVerifier codeVerifier() {
		TimeProvider timeProvider = new SystemTimeProvider();
		CodeGenerator codeGenerator = new DefaultCodeGenerator();
		return new DefaultCodeVerifier(codeGenerator, timeProvider);
	}

	@Bean
	public SecretGenerator secretGenerator() {
		return new DefaultSecretGenerator();
	}

	private static boolean checkRequestedResource(Resource requestedResource) {
		return requestedResource.exists() && requestedResource.isReadable();
	}

	private static ObjectMapper baseObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.registerModule(new JsonComponentModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		return mapper;
	}
}
