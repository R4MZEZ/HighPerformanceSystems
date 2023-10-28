package ru.itmo.hotdogs.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "users")
@NoArgsConstructor
public class UserEntity {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String login;

	@Column
	private String password;

	@ManyToMany
	@JoinTable(
		name = "users_roles",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<RoleEntity> roles;

	public UserEntity(String login, String password, Set<RoleEntity> roles) {
		this.login = login;
		this.password = password;
		this.roles = roles;
	}
}
