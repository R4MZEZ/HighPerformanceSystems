package ru.itmo.hotdogs.model.dto;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DogDto {
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z]+$")
	String name;
	@NotNull
	@Range(min = 0, max = 30)
	Integer age;
	@NotBlank
	String breed;
	@NotBlank
	String ownerLogin;
	Map<String, Integer> interests;

}
