package ru.itmo.hotdogs.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.feign.FeignConfig;
import ru.itmo.hotdogs.model.dto.ResponseDto;
import ru.itmo.hotdogs.model.dto.UserDto;
import ru.itmo.hotdogs.model.entity.UserEntity;

@FeignClient(name = "user-service",
	url = "localhost:8081",
	configuration = FeignConfig.class)
//@FeignClient(name = "user-service",
//	configuration = FeignConfig.class)
public interface UserApi {
	@PostMapping(path = "/users/new", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseDto<UserEntity> createNewUser(@RequestBody UserDto userDto);

	@GetMapping(path = "/users/find", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseDto<UserEntity> findByLogin(@RequestParam String login);

	@PostMapping("/users/addRole")
	Mono<ResponseDto<UserEntity>> addRole(@RequestParam Long userId, @RequestParam Integer roleId);

}
