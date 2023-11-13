package ru.itmo.hotdogs.exceptions;

public class NullRecommendationException extends Exception {

	public NullRecommendationException() {
		super("Сначала необходимо получить рекомендацию");
	}
}
