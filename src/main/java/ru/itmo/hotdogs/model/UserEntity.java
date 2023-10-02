package ru.itmo.hotdogs.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
@Entity
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Integer age;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  private OwnerEntity owner;

  @OneToMany(mappedBy = "user")
  private List<UsersInterestsEntity> userInterests;

  @ManyToMany()
  private Set<UserEntity> userMatches;

}
