package ru.itmo.hotdogs.model.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewDogDto extends NewUserDto{
	String name;
	Integer age;
	String breed;
	String ownerLogin;
	Map<String, Integer> interests;
}
