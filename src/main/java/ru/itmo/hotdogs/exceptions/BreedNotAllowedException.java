package ru.itmo.hotdogs.exceptions;

public class BreedNotAllowedException extends Exception{

	public BreedNotAllowedException() {
		super("Такая порода не разрешена на данной выставке");
	}
}
