package ru.itmo.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {


	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("hotdogs-client", r -> r.path("/dogs/**")
				.uri("lb://eclient"))

			.route("user-service", r -> r.path("/users/**", "/auth/**")
				.uri("lb://euser"))

//			.route("auth-service", r -> r.path("/auth/login")
//				.uri("lb://euser"))

			.route("owner-service", r -> r.path("/owners/**")
				.uri("lb://eowner"))

			.build();
	}
}
