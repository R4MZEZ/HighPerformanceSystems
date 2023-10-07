package ru.itmo.hotdogs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Entity
@Data
//@Table(name = "shows")
public class ShowEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column
  private Long prize;

  @Column
  private Date date;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  private OwnerEntity organizer;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = true)
  private UserEntity winner;

  @ManyToMany // TODO add arguments
  private List<BreedEntity> allowed_breeds;


}
