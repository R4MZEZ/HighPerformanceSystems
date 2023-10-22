package ru.itmo.hotdogs.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@Entity
@Table(name = "owners")
@NoArgsConstructor
public class OwnerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@OneToOne
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

	public OwnerEntity(UserEntity user, String name, String surname, Float balance,
		Point location) {
		this.user = user;
		this.name = name;
		this.surname = surname;
		this.location = location;

		this.balance = balance == null ? 0f : balance;
		this.reservedBalance = 0f;
	}
}
