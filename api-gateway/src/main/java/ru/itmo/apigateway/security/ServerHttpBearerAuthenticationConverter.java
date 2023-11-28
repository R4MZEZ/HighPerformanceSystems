package ru.itmo.apigateway.security;

import java.util.HashSet;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.itmo.apigateway.dto.UserDto;
import ru.itmo.apigateway.exceptions.JwtExpiredException;
import ru.itmo.apigateway.utils.JwtUtils;

@RequiredArgsConstructor
@Component
public class ServerHttpBearerAuthenticationConverter implements
	ServerAuthenticationConverter {

	private final JwtUtils jwtUtils;


	private UsernamePasswordAuthenticationToken getAuthorities(UserDto userDto) {
		return new UsernamePasswordAuthenticationToken(
			userDto, null,
			userDto.getRoles().stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList()));
	}

	public UserDto validateToken(String token) throws JwtExpiredException {

		if (jwtUtils.isExpired(token))
			throw new JwtExpiredException();

		UserDto userInfo = new UserDto();

		userInfo.setLogin(jwtUtils.getUsername(token));
		userInfo.setRoles(new HashSet<>(jwtUtils.getRoles(token)));
		return userInfo;
	}

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {
		String token = exchange.getRequest()
			.getHeaders()
			.getFirst(HttpHeaders.AUTHORIZATION);

		try {
			assert token != null;
			return Mono.just(validateToken(token.substring(7)))
				.map(this::getAuthorities);
		} catch (JwtExpiredException e) {
			throw new RuntimeException(e);
		}
	}
}
