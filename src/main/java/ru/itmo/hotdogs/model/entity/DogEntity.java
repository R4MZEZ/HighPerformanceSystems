package ru.itmo.hotdogs.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Entity
@Builder
@NoArgsConstructor
@Table(name = "dogs")
@AllArgsConstructor
public class DogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(nullable = false)
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z]+$")
	private String name;

	@Column(nullable = false)
	@Range(min = 0, max = 30)
	private Integer age;

	@ManyToOne
	@JoinColumn(name = "breed", nullable = false)
	private BreedEntity breed;

	@ManyToOne
	@JoinColumn(name = "owner", nullable = false)
	private OwnerEntity owner;

	@ManyToOne
	@JoinColumn(name = "cur_recommended")
	private DogEntity curRecommended;

	@OneToMany(mappedBy = "dog")
	private List<DogsInterestsEntity> interests;

	@ManyToMany
	@JoinTable(
		name = "dogs_matches",
		joinColumns = @JoinColumn(name = "dog1_id"),
		inverseJoinColumns = @JoinColumn(name = "dog2_id")
	)
	private Set<DogEntity> matches;

	@ManyToMany
	@JoinTable(
		name = "dogs_interactions",
		joinColumns = @JoinColumn(name = "sender_id"),
		inverseJoinColumns = @JoinColumn(name = "receiver_id")
	)
	private Set<DogsInteractionsEntity> interactions;

	@ManyToMany
	@JoinTable(
		name = "shows_participants",
		joinColumns = @JoinColumn(name = "dog_id"),
		inverseJoinColumns = @JoinColumn(name = "show_id")
	)
	private List<ShowEntity> appliedShows;

	public DogEntity(UserEntity user, String name, Integer age, BreedEntity breed,
		OwnerEntity owner) {
		this.user = user;
		this.name = name;
		this.age = age;
		this.breed = breed;
		this.owner = owner;
	}
}
