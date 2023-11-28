package ru.itmo.apigateway.security;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.itmo.apigateway.dto.UserDto;
import ru.itmo.apigateway.rest.AuthApi;

@Lazy
@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		UserDto user = (UserDto) authentication.getPrincipal();
		System.out.println(user.toString());
		return Mono.just(user).map(userDto -> authentication);
	}

}