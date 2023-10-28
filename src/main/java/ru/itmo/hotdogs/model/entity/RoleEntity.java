package ru.itmo.hotdogs.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z_]+$")
	private String name;


	public RoleEntity(String name) {
		this.name = name;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		Class<?> oEffectiveClass = o instanceof HibernateProxy
			? ((HibernateProxy) o).getHibernateLazyInitializer()
			.getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
			? ((HibernateProxy) this).getHibernateLazyInitializer()
			.getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) {
			return false;
		}
		RoleEntity role = (RoleEntity) o;
		return getId() != null && Objects.equals(getId(), role.getId());
	}

}
