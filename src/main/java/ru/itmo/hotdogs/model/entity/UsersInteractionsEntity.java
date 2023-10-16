package ru.itmo.hotdogs.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "users_interactions")
@NoArgsConstructor
public class UsersInteractionsEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
//  @JoinColumn(name = "sender_id")
  private UserEntity sender;

  @ManyToOne
//  @JoinColumn(name = "receiver_id")
  private UserEntity receiver;

  @Column(nullable = false)
  private Boolean is_liked;

  public UsersInteractionsEntity(UserEntity sender, UserEntity receiver, Boolean is_liked) {
    this.sender = sender;
    this.receiver = receiver;
    this.is_liked = is_liked;
  }
}

