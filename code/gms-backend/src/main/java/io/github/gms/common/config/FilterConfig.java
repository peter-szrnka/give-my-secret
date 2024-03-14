package io.github.gms.common.config;

import io.github.gms.common.filter.SetupFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Configuration
public class FilterConfig {
	
	@Bean
	public FilterRegistrationBean<SetupFilter> systemFilterBean(SetupFilter systemFilter) {
	    FilterRegistrationBean<SetupFilter> registrationBean = new FilterRegistrationBean<>();
	    registrationBean.setFilter(systemFilter);
	    registrationBean.addUrlPatterns("/setup/*");
	    return registrationBean;
	}

	/*@Bean
	public FilterRegistrationBean<IpRestrictionFilter> ipRestrictionFilterBean(IpRestrictionFilter ipRestrictionFilter) {
		FilterRegistrationBean<IpRestrictionFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(ipRestrictionFilter);
		registrationBean.addUrlPatterns("/api/secret/*");
		return registrationBean;
	}*/
}
