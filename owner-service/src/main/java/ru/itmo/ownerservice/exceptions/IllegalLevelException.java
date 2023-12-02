package ru.itmo.ownerservice.exceptions;

public class IllegalLevelException extends Exception {

	public IllegalLevelException() {
		super("Значение level должно быть в интервале (0;10].");
	}
}
