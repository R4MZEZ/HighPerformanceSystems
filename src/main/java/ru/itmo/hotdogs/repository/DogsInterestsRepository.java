package ru.itmo.hotdogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;
@Repository
public interface DogsInterestsRepository extends JpaRepository<DogsInterestsEntity, Long> {

}