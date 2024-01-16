package ru.itmo.ownerservice.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import ru.itmo.ownerservice.exceptions.AlreadyExistsException;
import ru.itmo.ownerservice.exceptions.NotFoundException;
import ru.itmo.ownerservice.feign.FeignConfig;
import ru.itmo.ownerservice.model.dto.ResponseDto;
import ru.itmo.ownerservice.model.dto.UserDto;
import ru.itmo.ownerservice.model.entity.UserEntity;

//@FeignClient(name = "user",
//	url = "user-service/users",
////	url = "localhost:8081/users",
//	configuration = FeignConfig.class)
@FeignClient(name = "user-service",
	url = "api-gateway:8081",
	configuration = FeignConfig.class)
public interface UserApi {

	@PostMapping(path = "/users/new", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	Mono<ResponseDto<UserEntity>> createNewUser(@RequestBody UserDto userDto) throws AlreadyExistsException;

	@GetMapping(path = "/users/find", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseDto<UserEntity> findByLogin(@RequestParam String login);

	@PostMapping("/users/addRole")
	Mono<ResponseDto<UserEntity>> addRole(@RequestParam Long userId, @RequestParam Integer roleId);


}
