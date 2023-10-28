package ru.itmo.hotdogs.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@Entity(name = "owners")
@NoArgsConstructor
public class OwnerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(nullable = false)
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z]+$")
	private String name;

	@Column
	@Pattern(regexp = "^[a-zA-Z]+$")
	private String surname;

	@Column
	@Min(0)
	private Float balance;

	@Column(name = "reserved_balance")
	@Min(0)
	private Float reservedBalance;

	@JsonIgnore
	@Column(columnDefinition = "geography", nullable = false)
	private Point location;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "organizer")
	private List<ShowEntity> shows;

	public OwnerEntity(UserEntity user, String name, String surname, Float balance,
		Point location) {
		this.user = user;
		this.name = name;
		this.surname = surname;
		this.location = location;

		this.balance = balance == null ? 0f : balance;
		this.reservedBalance = 0f;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		OwnerEntity owner = (OwnerEntity) o;

		if (!id.equals(owner.id)) {
			return false;
		}
		if (!name.equals(owner.name)) {
			return false;
		}
		if (!Objects.equals(surname, owner.surname)) {
			return false;
		}
		if (!balance.equals(owner.balance)) {
			return false;
		}
		if (!reservedBalance.equals(owner.reservedBalance)) {
			return false;
		}
		return location.equals(owner.location);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + (surname != null ? surname.hashCode() : 0);
		result = 31 * result + balance.hashCode();
		result = 31 * result + reservedBalance.hashCode();
		result = 31 * result + location.hashCode();
		return result;
	}
}
