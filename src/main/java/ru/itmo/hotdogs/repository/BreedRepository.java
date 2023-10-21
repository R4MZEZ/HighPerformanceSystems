package ru.itmo.hotdogs.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.entity.BreedEntity;

@Repository
public interface BreedRepository extends JpaRepository<BreedEntity, Integer> {
	Optional<BreedEntity> findByName(String name);
}