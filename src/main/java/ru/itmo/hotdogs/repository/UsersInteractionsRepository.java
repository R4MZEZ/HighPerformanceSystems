package ru.itmo.hotdogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.model.entity.UsersInteractionsEntity;

public interface UsersInteractionsRepository extends JpaRepository<UsersInteractionsEntity, Long> {

  UsersInteractionsEntity findBySenderAndReceiver(UserEntity sender, UserEntity receiver);
}