package ru.itmo.hotdogs.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "dogs_interactions")
@NoArgsConstructor
public class DogsInteractionsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
//  @JoinColumn(name = "sender_id")
	private DogEntity sender;

	@ManyToOne
//  @JoinColumn(name = "receiver_id")
	private DogEntity receiver;

	@Column(nullable = false)
	private Boolean is_liked;

	public DogsInteractionsEntity(DogEntity sender, DogEntity receiver, Boolean is_liked) {
		this.sender = sender;
		this.receiver = receiver;
		this.is_liked = is_liked;
	}
}

