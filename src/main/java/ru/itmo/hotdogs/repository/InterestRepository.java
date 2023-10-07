package ru.itmo.hotdogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.InterestEntity;

@Repository
public interface InterestRepository extends JpaRepository<InterestEntity, Integer> {

}