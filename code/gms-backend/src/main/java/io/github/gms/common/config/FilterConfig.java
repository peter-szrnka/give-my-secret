package io.github.gms.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.gms.common.filter.ApiHeaderInitializerFilter;
import io.github.gms.common.filter.SetupFilter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<ApiHeaderInitializerFilter> apiHeaderInitializerFilterBean(ApiHeaderInitializerFilter apiHeaderInitializerFilter) {
	    FilterRegistrationBean<ApiHeaderInitializerFilter> registrationBean = new FilterRegistrationBean<>();
	    registrationBean.setFilter(apiHeaderInitializerFilter);
	    registrationBean.addUrlPatterns("/api/*");
	    return registrationBean;
	}
	
	@Bean
	public FilterRegistrationBean<SetupFilter> systemFilterBean(SetupFilter systemFilter) {
	    FilterRegistrationBean<SetupFilter> registrationBean = new FilterRegistrationBean<>();
	    registrationBean.setFilter(systemFilter);
	    registrationBean.addUrlPatterns("/setup/*");
	    return registrationBean;
	}
}
