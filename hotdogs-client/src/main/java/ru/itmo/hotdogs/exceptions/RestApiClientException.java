package ru.itmo.hotdogs.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RestApiClientException extends Exception {

	public RestApiClientException(String message, Exception exception) {
		super(message, exception);
	}
}