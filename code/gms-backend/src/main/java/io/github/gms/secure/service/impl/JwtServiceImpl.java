package io.github.gms.secure.service.impl;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.gms.common.model.GenerateJwtRequest;
import io.github.gms.secure.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class JwtServiceImpl implements JwtService {

	@Value("${config.jwt.secret}")
	private String secret;

	@Override
	public String generateJwt(GenerateJwtRequest request) {
		Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), getAlgorithmByName(request.getAlgorithm()).getJcaName());

		Instant now = Instant.now();
		return Jwts.builder()
				.setClaims(request.getClaims())
				.setSubject(request.getSubject())
				.setId(UUID.randomUUID().toString()).setIssuedAt(Date.from(now))
				.setExpiration(Date.from(now.plus(request.getExpirationDateInSeconds(), ChronoUnit.SECONDS))).signWith(hmacKey).compact();
	}

	@Override
	public Claims parseJwt(String jwtToken, String algorithm) {
		Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), getAlgorithmByName(algorithm).getJcaName());
		return Jwts.parserBuilder().setSigningKey(hmacKey).build().parseClaimsJws(jwtToken).getBody();
	}
	
	private static SignatureAlgorithm getAlgorithmByName(String name) {
		return SignatureAlgorithm.forName(name);
	}
}