package ru.itmo.hotdogs.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "users")
public class UserEntity {

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
  @JsonIgnore
  private OwnerEntity owner;

  @ManyToOne
  @JoinColumn(name = "cur_recommended")
  private UserEntity curRecommended;

  @OneToMany(mappedBy = "user")
  private List<UsersInterestsEntity> userInterests;

  @ManyToMany // TODO add arguments
  private Set<UserEntity> userMatches;

  @ManyToMany // TODO add arguments
  private Set<UserEntity> userLikes;

}
