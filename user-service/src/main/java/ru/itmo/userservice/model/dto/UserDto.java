package ru.itmo.userservice.model.dto;

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
public class UserDto implements Serializable {
	@NotBlank
	String login;

	@NotBlank
	String password;

	@NotEmpty
	Set<Integer> roles;

}
