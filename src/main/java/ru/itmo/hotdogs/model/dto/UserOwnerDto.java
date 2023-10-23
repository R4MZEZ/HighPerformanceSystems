package ru.itmo.hotdogs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOwnerDto {
	String userLogin;
	String password;
	String name;
	String surname;
	Float balance;
	Double latitude;
	Double longitude;
	Boolean isOrganizer;


	public NewOwnerDto getOwnerDto(){
		return new NewOwnerDto(name,surname,balance,latitude,longitude, isOrganizer);
	}

	public NewUserDto getUserDto(){
		return new NewUserDto(userLogin, password);
	}
}
