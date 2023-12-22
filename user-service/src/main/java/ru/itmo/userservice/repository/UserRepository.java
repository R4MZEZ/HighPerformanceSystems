package ru.itmo.userservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.itmo.userservice.model.entity.UserEntity;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {
//	@Query(value = "select * from users where login = :login")
	Mono<UserEntity> findByLogin(@Param("login") String login);

	@Query("insert into users_roles(user_id, role_id) values (:user_id, :role_id)")
	Mono<UserEntity> addRole(@Param("user_id") Long userId, @Param("role_id") Long roleId);

	@Query("insert into users(login, password) VALUES (:login, :password)")
	Mono<UserEntity> save(@Param("login") String login, @Param("password") String password);

	Mono<UserEntity> deleteByLogin(String login);

}