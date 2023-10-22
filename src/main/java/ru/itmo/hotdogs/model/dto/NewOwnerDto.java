package ru.itmo.hotdogs.model.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewOwnerDto extends NewUserDto{
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z]+$")
	String name;
	@Pattern(regexp = "^[a-zA-Z]+$")
	String surname;
	@Min(0)
	Float balance;
	Double latitude;
	Double longitude;
	Boolean isOrganizer;

	public NewOwnerDto(@NotBlank String login, @NotBlank String password, String name,
		String surname,
		Float balance, Double latitude, Double longitude, Boolean isOrganizer) {
		super(login, password);
		this.name = name;
		this.surname = surname;
		this.balance = balance;
		this.latitude = latitude;
		this.longitude = longitude;
		this.isOrganizer = isOrganizer;
	}
}
