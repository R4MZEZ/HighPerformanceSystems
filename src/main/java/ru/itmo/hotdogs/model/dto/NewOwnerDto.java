package ru.itmo.hotdogs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewOwnerDto extends NewUserDto{
	String name;
	String surname;
	Float balance;
	Double latitude;
	Double longitude;
}
