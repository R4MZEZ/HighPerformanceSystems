package ru.itmo.hotdogs.model.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@Entity(name = "dogs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(nullable = false)
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z]+$")
	private String name;

	@Column(nullable = false)
	@Range(min = 0, max = 30)
	private Integer age;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "breed", nullable = false)
	private BreedEntity breed;

	@ManyToOne
	@JoinColumn(name = "owner", nullable = false)
	private OwnerEntity owner;

	@ManyToOne
	@JoinColumn(name = "cur_recommended")
	private DogEntity curRecommended;


	@OneToMany(mappedBy = "dog", cascade = CascadeType.ALL)
	private List<DogsInterestsEntity> interests;

	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(
		name = "dogs_matches",
		joinColumns = @JoinColumn(name = "dog1_id"),
		inverseJoinColumns = @JoinColumn(name = "dog2_id")
	)
	private Set<DogEntity> matches;

	@OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<DogsInteractionsEntity> interactions;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DogEntity dog = (DogEntity) o;

		if (!id.equals(dog.id)) {
			return false;
		}
		if (!name.equals(dog.name)) {
			return false;
		}
		if (!Objects.equals(age, dog.age)) {
			return false;
		}
		if (!breed.equals(dog.breed)) {
			return false;
		}
		return owner.equals(dog.owner);
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + (age != null ? age.hashCode() : 0);
		result = 31 * result + breed.hashCode();
		result = 31 * result + owner.hashCode();
		return result;
	}
}
