package ru.itmo.hotdogs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;

@Entity
@Data
@IdClass(UsersInterestsEntityId.class)
@Table(name = "users_interests")
public class UsersInterestsEntity{

  @Id
  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Id
  @ManyToOne
  @JoinColumn(name = "interest_id")
  private InterestEntity interest;

  @Column(nullable = false)
  private Integer level;

}
