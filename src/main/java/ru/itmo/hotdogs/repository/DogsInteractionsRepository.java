package ru.itmo.hotdogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInteractionsEntity;
@Repository
public interface DogsInteractionsRepository extends JpaRepository<DogsInteractionsEntity, Long> {

	DogsInteractionsEntity findBySenderAndReceiver(DogEntity sender, DogEntity receiver);
}