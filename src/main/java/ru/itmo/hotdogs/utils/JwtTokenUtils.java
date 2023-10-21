package ru.itmo.hotdogs.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtils {

	@Value("${jwt.secret}")
	private String secret;


	@Value("${jwt.lifetime}")
	private Duration lifetime;

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> credentials = new HashMap<>();
		List<String> roles = userDetails.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority).toList();
		credentials.put("roles", roles);
		Date issuedDate = new Date();
		Date expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());

		return Jwts.builder()
			.setClaims(credentials)
			.setSubject(userDetails.getUsername())
			.setIssuedAt(issuedDate)
			.setExpiration(expiredDate)
			.signWith(SignatureAlgorithm.HS256, secret)
			.compact();
	}

	public String getUsername(String token) {
		return getAllClaimsFromToken(token).getSubject();
	}

	public List<String> getRoles(String token) {
		return getAllClaimsFromToken(token).get("roles", List.class);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser()
			.setSigningKey(secret)
			.parseClaimsJws(token)
			.getBody();
	}
}
