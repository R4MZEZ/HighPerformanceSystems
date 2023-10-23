package ru.itmo.hotdogs.model.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDogDto {
	String userLogin;
	String password;
	String name;
	Integer age;
	String breed;
	String ownerLogin;
	Map<String, Integer> interests;

	public NewDogDto getDogDto(){
		return new NewDogDto(name,age,breed,ownerLogin,interests);
	}

	public NewUserDto getUserDto(){
		return new NewUserDto(userLogin, password);
	}
}
