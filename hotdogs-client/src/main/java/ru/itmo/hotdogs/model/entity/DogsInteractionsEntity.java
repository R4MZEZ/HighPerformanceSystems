package ru.itmo.hotdogs.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "dogs_interactions")
@Getter
@Setter
@NoArgsConstructor
public class DogsInteractionsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "sender_id")
	private DogEntity sender;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "receiver_id")
	private DogEntity receiver;

	@Column(name = "is_liked", nullable = false)
	private Boolean isLiked;

	public DogsInteractionsEntity(DogEntity sender, DogEntity receiver, Boolean isLiked) {
		this.sender = sender;
		this.receiver = receiver;
		this.isLiked = isLiked;
	}
}
