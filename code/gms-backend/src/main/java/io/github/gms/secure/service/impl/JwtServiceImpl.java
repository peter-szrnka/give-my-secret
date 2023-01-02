package io.github.gms.secure.service.impl;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.util.Constants;
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
	
	private static final SignatureAlgorithm ALGORYTHM = SignatureAlgorithm.HS512;

	@Value("${config.jwt.secret}")
	private String secret;

	@Override
	public String generateJwt(GmsUserDetails user) {
		Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), ALGORYTHM.getJcaName());

		Instant now = Instant.now();
		return Jwts.builder()
				.claim(MdcParameter.USER_ID.getDisplayName(), user.getUserId())
				.claim(MdcParameter.USER_NAME.getDisplayName(), user.getUsername())
				.claim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
				.setSubject(user.getUsername())
				.setId(UUID.randomUUID().toString()).setIssuedAt(Date.from(now))
				.setExpiration(Date.from(now.plus(Constants.VALIDITY_SECONDS, ChronoUnit.SECONDS))).signWith(hmacKey).compact();
	}

	@Override
	public Claims parseJwt(String jwtToken) {
		Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), ALGORYTHM.getJcaName());
		return Jwts.parserBuilder().setSigningKey(hmacKey).build().parseClaimsJws(jwtToken).getBody();
	}
}
