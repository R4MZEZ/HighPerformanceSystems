package ru.itmo.hotdogs.model.dto;

import java.util.Objects;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDto {

	@NotBlank
	@Pattern(regexp = "^[a-zA-Z]+$")
	String name;
	@Pattern(regexp = "^[a-zA-Z]+$")
	String surname;
	@Min(0)
	Float balance;
	Double latitude;
	Double longitude;
	Boolean isOrganizer;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		OwnerDto that = (OwnerDto) o;
		return Objects.equals(name, that.name) && Objects.equals(surname,
			that.surname) && Objects.equals(balance, that.balance)
			&& Objects.equals(latitude, that.latitude) && Objects.equals(longitude,
			that.longitude) && Objects.equals(isOrganizer, that.isOrganizer);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, surname, balance, latitude, longitude, isOrganizer);
	}
}
