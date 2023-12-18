package ru.itmo.userservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.itmo.userservice.model.entity.UserEntity;

@Repository
public interface UserRepository extends R2dbcRepository<UserEntity, Long> {
	@Query(value = "select * from users where login = :login")
	Mono<UserEntity> findByLogin(@Param("login") String login);

	Mono<UserEntity> deleteByLogin(String login);

}