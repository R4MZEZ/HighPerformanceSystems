package ru.itmo.ownerservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "shows")
@Getter
@Setter
@NoArgsConstructor
public class ShowEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(nullable = false)
	@Min(0)
	private Long prize;

	@Column(nullable = false)
	private Timestamp date;

	@ManyToOne
	@JoinColumn(name = "organizer", nullable = false)
	@JsonIgnoreProperties(value = "shows", allowSetters = true)
	private OwnerEntity organizer;

	@ManyToOne
	@JoinColumn(name = "winner")
	@JsonIgnore
	private DogEntity winner;

	@ManyToMany(fetch = FetchType.EAGER)
	@NotEmpty
	@JoinTable(
		name = "allowed_breeds",
		joinColumns = @JoinColumn(name = "show_id"),
		inverseJoinColumns = @JoinColumn(name = "breed_id")
	)
	private Set<BreedEntity> allowedBreeds;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "shows_participants",
		joinColumns = @JoinColumn(name = "show_id"),
		inverseJoinColumns = @JoinColumn(name = "dog_id")
	)
	@JsonIgnoreProperties(value = "appliedShows", allowSetters = true)
	private List<DogEntity> participants;

	public ShowEntity(Long prize, Timestamp date, OwnerEntity organizer,
		Set<BreedEntity> allowedBreeds) {
		this.prize = prize;
		this.date = date;
		this.organizer = organizer;
		this.allowedBreeds = allowedBreeds;
		this.participants = new ArrayList<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ShowEntity show = (ShowEntity) o;

		if (!id.equals(show.id)) {
			return false;
		}
		if (!prize.equals(show.prize)) {
			return false;
		}
		return date.equals(show.date);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + prize.hashCode();
		result = 31 * result + date.hashCode();
		return result;
	}
}
