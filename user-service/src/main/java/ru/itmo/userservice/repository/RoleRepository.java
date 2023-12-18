package ru.itmo.userservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.userservice.model.entity.RoleEntity;

public interface RoleRepository extends R2dbcRepository<RoleEntity, Long> {

	@Query(value = "select role_id, name from users_roles inner join roles on role_id = roles.id inner join users on user_id = users.id where login = :login")
	Flux<RoleEntity> findUserRolesByLogin(@Param("login") String login);

	@Query(value = "select * from roles where name = :name")
	Mono<RoleEntity> findByName(@Param("name")String name);

	@Query(value = "insert into roles values ")
	Mono<RoleEntity> save(@Param("role") RoleEntity role);
}