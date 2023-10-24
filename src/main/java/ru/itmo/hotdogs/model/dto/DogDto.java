package ru.itmo.hotdogs.model.dto;

import java.util.Map;
import javax.validation.constraints.NotBlank;
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
	@Range(min = 0, max = 30)
	Integer age;
	String breed;
	String ownerLogin;
	Map<String, Integer> interests;

}
