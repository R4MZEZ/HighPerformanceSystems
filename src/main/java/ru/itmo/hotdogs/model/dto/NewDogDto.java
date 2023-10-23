package ru.itmo.hotdogs.model.dto;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewDogDto extends NewUserDto{
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z]+$")
	String name;
	@Range(min = 0, max = 30)
	Integer age;
	String breed;
	String ownerLogin;
	Map<String, Integer> interests;

	public NewDogDto(@NotBlank String login, @NotBlank String password, String name, Integer age,
		String breed, String ownerLogin, Map<String, Integer> interests) {
		super(login, password);
		this.name = name;
		this.age = age;
		this.breed = breed;
		this.ownerLogin = ownerLogin;
		this.interests = interests;
	}
}