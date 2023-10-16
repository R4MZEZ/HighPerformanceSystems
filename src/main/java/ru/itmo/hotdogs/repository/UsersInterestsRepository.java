package ru.itmo.hotdogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.hotdogs.model.entity.UsersInterestsEntity;

public interface UsersInterestsRepository extends JpaRepository<UsersInterestsEntity, Long> {

}