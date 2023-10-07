package ru.itmo.hotdogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.ShowEntity;

@Repository
public interface ShowRepository extends JpaRepository<ShowEntity, Long> {

}