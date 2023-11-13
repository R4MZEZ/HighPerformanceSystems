package ru.itmo.hotdogs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtRequest {
	private String login;
	private String password;

}
