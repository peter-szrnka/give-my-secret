package io.github.gms.common.service;

import com.google.common.collect.Maps;
import io.github.gms.common.enums.JwtConfigType;
import io.github.gms.common.model.GenerateJwtRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Service
public class JwtService {

	private final Clock clock;
	private final String secret;

	public JwtService(Clock clock, @Value("${config.jwt.secret}") String secret) {
		this.clock = clock;
		this.secret = secret;
	}

	public Map<JwtConfigType, String> generateJwts(Map<JwtConfigType, GenerateJwtRequest> request) {
		Map<JwtConfigType, String> result = Maps.newHashMap();
		request.forEach((key, value) -> result.put(key, generateJwt(value)));
		return result;
	}

	public String generateJwt(GenerateJwtRequest request) {
		Key key = getKey(request.getAlgorithm());

		Instant now = clock.instant();
		return Jwts.builder()
				.setClaims(request.getClaims())
				.setSubject(request.getSubject())
				.setId(UUID.randomUUID().toString()).setIssuedAt(Date.from(now))
				.setExpiration(Date.from(now.plus(request.getExpirationDateInSeconds(), ChronoUnit.SECONDS))).signWith(key).compact();
	}

	public Claims parseJwt(String jwtToken, String algorithm) {
		Key key = getKey(algorithm);
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken).getBody();
	}

	private Key getKey(String algorithmName) {
		return new SecretKeySpec(Base64.getDecoder().decode(secret), getAlgorithmByName(algorithmName).getJcaName());
	}

	private static SignatureAlgorithm getAlgorithmByName(String name) {
		return SignatureAlgorithm.forName(name);
	}
}
