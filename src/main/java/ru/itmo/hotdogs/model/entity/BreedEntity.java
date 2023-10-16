package ru.itmo.hotdogs.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "breeds")
@NoArgsConstructor
public class BreedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Integer id;

	@Column(nullable = false)
	private String name;
//
//	@ManyToMany
//	@JoinTable(
//		name = "allowed_breeds",
//		joinColumns = @JoinColumn(name = "breed_id")
//	)
//	private Set<ShowEntity> show;

	public BreedEntity(String name) {
		this.name = name;
	}
}
