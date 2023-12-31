package ru.itmo.ownerservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistsException extends Exception {

	public AlreadyExistsException(String message) {
		super(message);
	}
}
