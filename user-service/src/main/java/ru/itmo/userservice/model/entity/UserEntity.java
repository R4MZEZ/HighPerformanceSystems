package ru.itmo.userservice.model.entity;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("users")
public class UserEntity {

	@Id
	private Long id;
	private String login;
	private String password;

	public UserEntity(Long id) {
		this.id = id;
	}
}
