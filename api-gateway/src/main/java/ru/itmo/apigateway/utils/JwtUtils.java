package ru.itmo.apigateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.itmo.apigateway.dto.UserDto;
import ru.itmo.apigateway.exceptions.JwtExpiredException;
import ru.itmo.apigateway.exceptions.UnauthorizedException;

@Component
public class JwtUtils {

	@Value("${jwt.secret}")
	private String secret;


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

	public boolean isExpired(String token) {
		return getAllClaimsFromToken(token).getExpiration().before(new Date());

	}

	public Mono<UserDto> check(String accessToken) {
		return Mono.just(validateToken(accessToken))
			.onErrorResume(e -> Mono.error(new UnauthorizedException(e.getMessage())));
	}

	public Mono<Authentication> getAuthorities(UserDto userDto) {
		return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(
			userDto, userDto,
			userDto.getRoles().stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList())));
	}

	public UserDto validateToken(String token) throws JwtExpiredException {

		try {
			isExpired(token);
		}catch (ExpiredJwtException e){
			throw new JwtExpiredException();
		}

		UserDto userInfo = new UserDto();

		userInfo.setLogin(getUsername(token));
		userInfo.setRoles(new HashSet<>(getRoles(token)));
		return userInfo;
	}
}
