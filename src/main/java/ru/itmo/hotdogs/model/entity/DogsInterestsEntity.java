package ru.itmo.hotdogs.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Entity
@Data
@Table(name = "dogs_interests")
@NoArgsConstructor
public class DogsInterestsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "dog_id")
	@JsonIgnore
	private DogEntity dog;

	@ManyToOne
	@JoinColumn(name = "interest_id")
	private InterestEntity interest;

	@Range(min=1, max=10)
	@Column(nullable = false)
	private Integer level;

	public DogsInterestsEntity(DogEntity dog, InterestEntity interest, Integer level) {
		this.dog = dog;
		this.interest = interest;
		this.level = level;
	}
}
