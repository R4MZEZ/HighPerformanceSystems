package ru.itmo.ownerservice.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.ownerservice.model.entity.OwnerEntity;
import ru.itmo.ownerservice.model.entity.UserEntity;

@Repository
public interface OwnerRepository extends JpaRepository<OwnerEntity, Long> {
	Optional<OwnerEntity> findByUser(UserEntity userEntity);

	Page<OwnerEntity> findAll(Pageable pageable);
}