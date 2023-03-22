package io.github.gms.api.controller;

import io.github.gms.api.service.ApiService;
import io.github.gms.secure.dto.ApiResponseDto;
import io.github.gms.secure.dto.GetSecretRequestDto;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
public class ApiController {
	
	public static final String API_KEY_HEADER = "x-api-key";
	private final ApiService service;

	public ApiController(ApiService service) {
		this.service = service;
	}

	@GetMapping(path = "/api/secret/{secretId}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public @ResponseBody ApiResponseDto getSecret(@RequestHeader(name = API_KEY_HEADER, required = true) String apiKey, @PathVariable(name = "secretId") String secretId) {
		return service.getSecret(new GetSecretRequestDto(apiKey, secretId));
	}
}