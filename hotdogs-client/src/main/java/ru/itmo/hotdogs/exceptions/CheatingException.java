package ru.itmo.hotdogs.exceptions;

public class CheatingException extends Exception{

	public CheatingException() {
		super("Вы не можете участвовать в выставке, которую организовал ваш хозяин.");
	}
}
