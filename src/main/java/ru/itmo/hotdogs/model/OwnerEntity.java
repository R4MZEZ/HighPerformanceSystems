package ru.itmo.hotdogs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "owners")
public class OwnerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column
  private String surname;

  @Column(nullable = false)
  private Boolean is_organizer;

  @ManyToOne
  @JoinColumn(name = "coordinates", nullable = false)
  private CoordinatesEntity coordinates;

}
