package ru.itmo.hotdogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.BreedEntity;

@Repository
public interface BreedRepository extends JpaRepository<BreedEntity, Integer> {

}