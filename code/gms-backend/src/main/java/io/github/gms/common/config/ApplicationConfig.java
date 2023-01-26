package io.github.gms.common.config;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Configuration
@EnableScheduling
@EnableAsync
@EnableJpaRepositories(basePackages = "io.github.gms")
public class ApplicationConfig implements WebMvcConfigurer {

	@Bean("secretRotationExecutor")
    public TaskExecutor getAsyncExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("secret-rotation-");
        return executor;
    }
	
	@Bean
	Gson getGson() {
		return new GsonBuilder()
				.serializeNulls()
				.enableComplexMapKeySerialization()
				.setLenient()
				.registerTypeAdapter(LocalDateTime.class,
						(JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
				.registerTypeAdapter(LocalDateTime.class,
						(JsonSerializer<LocalDateTime>) (json, typeOfT, context) -> new JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(json)))
				
				.registerTypeAdapter(ZonedDateTime.class,
						(JsonDeserializer<ZonedDateTime>) (json, typeOfT, context) -> ZonedDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
				.registerTypeAdapter(ZonedDateTime.class,
						(JsonSerializer<ZonedDateTime>) (json, typeOfT, context) -> new JsonPrimitive(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(json)))
				.create();
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplateBuilder().build();
	}
	
	@Bean
	Clock clock() {
		return Clock.systemDefaultZone();
	}
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);

                        return requestedResource.exists() && requestedResource.isReadable() ? requestedResource
                                : new ClassPathResource("/static/index.html");
                    }
                });
    }

	@Bean
    public TomcatServletWebServerFactory containerFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void customizeConnector(Connector connector) {
                super.customizeConnector(connector);
                
                addContextCustomizers(context -> context.setCookieProcessor(new LegacyCookieProcessor()));
            }
        };
    }
}
