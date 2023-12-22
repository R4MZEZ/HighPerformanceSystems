package ru.itmo.apigateway.security;

import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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

	private static final String BEARER = "Bearer ";
	private static final Predicate<String> matchBearerLength = authValue -> authValue.length()
		> BEARER.length();
	private static final Function<String, Mono<String>> isolateBearerValue = authValue -> Mono.justOrEmpty(
		authValue.substring(BEARER.length()));

	private final JwtUtils jwtUtils;

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {

		return Mono.justOrEmpty(exchange)
			.flatMap(ServerHttpBearerAuthenticationConverter::extract)
			.filter(matchBearerLength)
			.flatMap(isolateBearerValue)
			.flatMap(jwtUtils::check)
			.flatMap(jwtUtils::getAuthorities);
	}

	public static Mono<String> extract(ServerWebExchange serverWebExchange) {
		return Mono.justOrEmpty(serverWebExchange.getRequest()
			.getHeaders()
			.getFirst(HttpHeaders.AUTHORIZATION));
	}
}
