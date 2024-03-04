package io.github.gms.functions.api;

import io.github.gms.functions.secret.GetSecretRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static io.github.gms.common.util.Constants.API_KEY_HEADER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
public class ApiController {

	private final ApiService service;

	@GetMapping(path = "/api/secret/{secretId}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public Map<String, String> getSecret(@RequestHeader(name = API_KEY_HEADER) String apiKey, @PathVariable(name = "secretId") String secretId) {
		return service.getSecret(new GetSecretRequestDto(apiKey, secretId));
	}
}