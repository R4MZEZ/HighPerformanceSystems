package ru.itmo.apigateway.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtExpiredException extends ApiException{

	public JwtExpiredException() {
		super("Token expired", "UNAUTHORIZED");
	}
}
