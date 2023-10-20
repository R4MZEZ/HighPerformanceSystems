package ru.itmo.hotdogs.model.dto;

import lombok.Data;

@Data
public class JwtRequest {
	private String login;
	private String password;

}
