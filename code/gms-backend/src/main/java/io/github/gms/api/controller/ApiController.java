package io.github.gms.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.gms.api.service.ApiService;
import io.github.gms.secure.dto.ApiResponseDto;
import io.github.gms.secure.dto.GetSecretRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@RestController
public class ApiController {
	
	@Autowired
	private ApiService service;

	@GetMapping(path = "/api/secret/{secretId}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public @ResponseBody ApiResponseDto getSecret(@RequestHeader(name = "X-API-KEY") String apiKey, @PathVariable(name = "secretId") String secretId) {
		return new ApiResponseDto(service.getSecret(new GetSecretRequestDto(apiKey, secretId)));
	}
}