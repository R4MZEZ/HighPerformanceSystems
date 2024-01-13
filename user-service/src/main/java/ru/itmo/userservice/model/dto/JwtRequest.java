package ru.itmo.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на генерацию токена")
public class JwtRequest {
	@Schema(description = "Логин")
	private String login;
	@Schema(description = "Пароль")
	private String password;

}
