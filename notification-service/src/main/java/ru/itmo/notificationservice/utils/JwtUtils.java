package ru.itmo.notificationservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

	@Value("${secret}")
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

	public String getUsernameFromHeader(String authHeader) throws IllegalArgumentException{
		try {
			String jwt = authHeader.replace("Bearer ", "");
			return getUsername(jwt);
		}catch (NullPointerException ex){
			throw new IllegalArgumentException();
		}
	}

}