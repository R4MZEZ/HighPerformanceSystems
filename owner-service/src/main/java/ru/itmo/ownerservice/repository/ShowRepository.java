package ru.itmo.ownerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.ownerservice.model.entity.ShowEntity;


@Repository
public interface ShowRepository extends JpaRepository<ShowEntity, Long> {
}