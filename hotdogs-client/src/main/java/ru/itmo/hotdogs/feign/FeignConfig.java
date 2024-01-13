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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignConfig {
	@Bean
	public ErrorDecoder errorDecoder(){
		return new CustomErrorDecoder();
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
