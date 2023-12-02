package ru.itmo.ownerservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationOwnerDto {
	UserDto userInfo;
	OwnerDto ownerInfo;
}
