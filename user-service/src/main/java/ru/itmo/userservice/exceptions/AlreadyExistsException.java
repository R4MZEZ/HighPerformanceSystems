package ru.itmo.userservice.exceptions;

public class AlreadyExistsException extends Exception {

	public AlreadyExistsException(String message) {
		super(message);
	}
}
