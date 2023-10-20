package ru.itmo.hotdogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;

public interface DogsInterestsRepository extends JpaRepository<DogsInterestsEntity, Long> {

}