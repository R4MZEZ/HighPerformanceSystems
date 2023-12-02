package ru.itmo.ownerservice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

	@Value("${jwt-secret}")
	private String secret;


	private String getUsername(String token) {
		return getAllClaimsFromToken(token).getSubject();
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser()
			.setSigningKey(secret)
			.parseClaimsJws(token)
			.getBody();
	}

	public String getUsernameFromRequest(ServerHttpRequest request){
		String jwt = request.getHeaders().getFirst("Authorization").replace("Bearer ", "");
		return getUsername(jwt);
	}

}
