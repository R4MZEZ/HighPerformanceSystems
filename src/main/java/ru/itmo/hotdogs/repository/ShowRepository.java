package ru.itmo.hotdogs.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.entity.ShowEntity;

@Repository
public interface ShowRepository extends JpaRepository<ShowEntity, Long> {
//	Optional<ShowEntity> findById(Long id);
}