package ru.itmo.hotdogs.model.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dogs")
public class DogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
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
	private Set<DogEntity> interactions;

}
