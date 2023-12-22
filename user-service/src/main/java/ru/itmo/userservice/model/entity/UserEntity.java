package ru.itmo.userservice.model.entity;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

	private Long id;
	private String login;
	private String password;
	private Set<RoleEntity> roles;
	public UserEntity(String login, String password, Set<RoleEntity> roles) {
		this.login = login;
		this.password = password;
		this.roles = roles;
	}
}
