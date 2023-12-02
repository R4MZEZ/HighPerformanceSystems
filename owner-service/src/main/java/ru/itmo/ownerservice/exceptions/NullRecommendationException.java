package ru.itmo.ownerservice.exceptions;

public class NullRecommendationException extends Exception {

	public NullRecommendationException() {
		super("Сначала необходимо получить рекомендацию");
	}
}
