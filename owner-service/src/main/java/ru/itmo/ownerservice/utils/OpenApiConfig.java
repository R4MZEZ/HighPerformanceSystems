package ru.itmo.ownerservice.utils;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(
		title = "Hotdogs",
		description = "Date application for dogs", version = "1.0.0",
		contact = @Contact(
			name = "Kazachenko Roman, Kolesnikova Svetlana",
			email = "312515@niuitmo.ru",
			url = "https://onlyfans.com/hotdogs"
		)
	)
)
public class OpenApiConfig {

}