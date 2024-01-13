package ru.itmo.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные для регистрации нового пользователя")
public class UserDto implements Serializable {
	@NotBlank
	@Schema(description = "Логин")
	String login;

	@NotBlank
	@Schema(description = "Пароль")
	String password;

	@NotEmpty
	@Schema(description = "Список ролей")
	Set<Integer> roles;

}
