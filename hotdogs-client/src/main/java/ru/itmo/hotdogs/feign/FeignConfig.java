package ru.itmo.hotdogs.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.servo.util.TimeLimiter;
import feign.FeignException;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignConfig {
//	@Bean
//	public ErrorDecoder errorDecoder(){
//		return new CustomErrorDecoder();
//	}

	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerFactoryCustomizer() {
		CircuitBreakerConfig circuitBreakerConfig =
			CircuitBreakerConfig.custom()
				.ignoreException(FeignException.ServiceUnavailable.class::isInstance)
				.waitDurationInOpenState(Duration.ofSeconds(20))
				.build();
		TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom().
			timeoutDuration(Duration.ofSeconds(20)).build();
		return factory -> factory
			.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(circuitBreakerConfig)
				.timeLimiterConfig(timeLimiterConfig).build());
	}

	@Bean
	public Decoder decoder(ObjectMapper objectMapper) {
		return new JacksonDecoder(objectMapper);
	}

	@Bean
	public Encoder encoder(ObjectMapper objectMapper) {
		return new JacksonEncoder(objectMapper);
	}

}
