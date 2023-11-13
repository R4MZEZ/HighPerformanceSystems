package ru.itmo.hotdogs.exceptions;

public class NotEnoughMoneyException extends Exception{

	public NotEnoughMoneyException() {
		super("Недостаточно средств на счету");
	}
}
