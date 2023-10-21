package ru.itmo.hotdogs.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.model.entity.UserEntity;

@Repository
public interface OwnerRepository extends JpaRepository<OwnerEntity, Long> {
	Optional<OwnerEntity> findByUser(UserEntity userEntity);
}