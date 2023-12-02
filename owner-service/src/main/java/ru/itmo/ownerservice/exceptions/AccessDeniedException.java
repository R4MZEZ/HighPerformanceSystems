package ru.itmo.ownerservice.exceptions;

public class AccessDeniedException extends Exception{

	public AccessDeniedException(String message) {
		super(message);
	}
}
