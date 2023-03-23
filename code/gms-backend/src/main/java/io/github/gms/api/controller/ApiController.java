package io.github.gms.api.controller;

import io.github.gms.api.service.ApiService;
import io.github.gms.secure.dto.ApiResponseDto;
import io.github.gms.secure.dto.GetSecretRequestDto;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static io.github.gms.common.util.Constants.API_KEY_HEADER;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
public class ApiController {

	private final ApiService service;

	public ApiController(ApiService service) {
		this.service = service;
	}

	@GetMapping(path = "/api/secret/{secretId}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public @ResponseBody ApiResponseDto getSecret(@RequestHeader(name = API_KEY_HEADER, required = true) String apiKey, @PathVariable(name = "secretId") String secretId) {
		return service.getSecret(new GetSecretRequestDto(apiKey, secretId));
	}
}