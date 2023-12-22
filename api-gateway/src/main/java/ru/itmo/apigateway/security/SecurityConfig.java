package ru.itmo.apigateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
@Configuration
public class SecurityConfig {

	private final ReactiveAuthenticationManager authenticationManager;
	private final ServerAuthenticationConverter converter;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
		http
			.authorizeExchange(exchange -> exchange

				.pathMatchers("/dogs/breeds/find/**").permitAll()
				.pathMatchers("/dogs/find/**").permitAll()
				.pathMatchers("/owners/shows/*/addParticipant").permitAll()
				.pathMatchers("/owners/find/**").permitAll()

				.pathMatchers("/owners/shows/**").hasRole("ORGANIZER")
				.pathMatchers("/owners/new").hasRole("ADMIN")
				.pathMatchers("/owners/**").hasAnyRole("OWNER", "ADMIN")
				.pathMatchers("/owners").hasRole("ADMIN")
				.pathMatchers("/dogs/new").hasRole("ADMIN")
				.pathMatchers("/dogs/test").hasRole("ADMIN")
				.pathMatchers("/dogs/breeds/new").hasRole("ADMIN")
				.pathMatchers("/dogs/interests/new").hasRole("ADMIN")
				.pathMatchers("/dogs/me").hasRole("DOG")
//				.pathMatchers("/dogs/**").hasAnyRole("DOG", "ADMIN")
				.pathMatchers("/dogs").hasRole("ADMIN")
				.anyExchange().permitAll())
			.exceptionHandling(
				exception -> exception
					.authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
					.accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))))
			.csrf(CsrfSpec::disable)
			.cors(CorsSpec::disable)
			.addFilterAt(bearerAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
			;

		return http.build();
	}

	AuthenticationWebFilter bearerAuthenticationFilter(ReactiveAuthenticationManager authManager) {
		AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(authManager);
		bearerAuthenticationFilter.setServerAuthenticationConverter(converter);
		bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(
			"/users/**", "/dogs/**", "breeds/**", "owners/**"));

		return bearerAuthenticationFilter;
	}


}
