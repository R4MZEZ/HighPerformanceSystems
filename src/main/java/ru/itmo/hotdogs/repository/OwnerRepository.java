package ru.itmo.hotdogs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.OwnerEntity;
@Repository
public interface OwnerRepository extends JpaRepository<OwnerEntity, Long> {

}