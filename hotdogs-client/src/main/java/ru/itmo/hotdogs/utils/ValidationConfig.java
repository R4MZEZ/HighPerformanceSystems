package ru.itmo.hotdogs.utils;

import javax.validation.Validation;
import javax.validation.Validator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {
	@Bean
	public Validator validator() {
		return Validation.byDefaultProvider()
			.configure()
			.messageInterpolator(new ParameterMessageInterpolator())
			.buildValidatorFactory()
			.getValidator();
	}
}