package ru.itmo.ownerservice.model.dto;

import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	@NotBlank
	String login;

	@NotBlank
	String password;

	@NotEmpty
	Set<Integer> roles;

	public UserDto(String login, String password) {
		this.login = login;
		this.password = password;
	}
}
