package ru.itmo.hotdogs.model.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDogDto {
	UserDto userInfo;
	DogDto dogInfo;
}
