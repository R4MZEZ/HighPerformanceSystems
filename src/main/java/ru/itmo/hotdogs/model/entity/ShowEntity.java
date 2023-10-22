package ru.itmo.hotdogs.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "shows")
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
	private Date date;

	@ManyToOne
	@JoinColumn(name = "organizer", nullable = false)
	private OwnerEntity organizer;

	@ManyToOne
	@JoinColumn(name = "winner")
	private DogEntity winner;

	@ManyToMany
	@NotEmpty
	@JoinTable(
		name = "allowed_breeds",
		joinColumns = @JoinColumn(name = "show_id"),
		inverseJoinColumns = @JoinColumn(name = "breed_id")
	)
	private Set<BreedEntity> allowedBreeds;

	@JsonIgnore
	@ManyToMany
	@JoinTable(
		name = "shows_participants",
		joinColumns = @JoinColumn(name = "show_id"),
		inverseJoinColumns = @JoinColumn(name = "dog_id")
	)
	private Set<DogEntity> participants;

	public ShowEntity(Long prize, Date date, OwnerEntity organizer,
		Set<BreedEntity> allowedBreeds) {
		this.prize = prize;
		this.date = date;
		this.organizer = organizer;
		this.allowedBreeds = allowedBreeds;
	}
}
