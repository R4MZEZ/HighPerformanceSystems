package ru.itmo.userservice.model.entity;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity {

	private Long id;

	@NotBlank
	@Pattern(regexp = "^[a-zA-Z_]+$")
	private String name;

	public RoleEntity(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RoleEntity that = (RoleEntity) o;

		if (!id.equals(that.id)) {
			return false;
		}
		return name.equals(that.name);
	}

}
