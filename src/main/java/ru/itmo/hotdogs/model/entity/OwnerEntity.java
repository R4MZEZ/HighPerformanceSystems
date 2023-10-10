package ru.itmo.hotdogs.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@Entity
@Table(name = "owners")
@NoArgsConstructor
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

  @Column(columnDefinition = "geograghy", nullable = false)
  private Point location;

  public OwnerEntity(String name, String surname, Boolean is_organizer, Point location) {
    this.name = name;
    this.surname = surname;
    this.is_organizer = is_organizer;
    this.location = location;
  }
}
