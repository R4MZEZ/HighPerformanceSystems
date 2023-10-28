package ru.itmo.hotdogs.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.entity.InterestEntity;

@Repository
public interface InterestRepository extends JpaRepository<InterestEntity, Integer> {
	Optional<InterestEntity> findByName(String name);

	Optional<InterestEntity> deleteByName(String name);

}