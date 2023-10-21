package ru.itmo.hotdogs.exceptions;

public class ShowDateExpiredException extends Exception{

	public ShowDateExpiredException() {
		super("Выставка уже прошла");
	}
}
