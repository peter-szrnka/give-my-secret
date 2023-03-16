package io.github.gms.common.config.cache;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

import io.github.gms.secure.dto.GetSecretRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class ApiCacheKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		GetSecretRequestDto request = (GetSecretRequestDto) params[0];
		return request.getApiKey() + "_" + request.getSecretId();
	}
}