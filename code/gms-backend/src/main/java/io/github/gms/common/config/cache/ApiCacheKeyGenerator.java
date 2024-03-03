package io.github.gms.common.config.cache;

import io.github.gms.functions.secret.GetSecretRequestDto;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class ApiCacheKeyGenerator implements KeyGenerator {

	@Override
	public @NonNull Object generate(@NonNull Object target, @NonNull Method method, Object... params) {
		GetSecretRequestDto request = (GetSecretRequestDto) params[0];
		return request.getApiKey() + "_" + request.getSecretId();
	}
}