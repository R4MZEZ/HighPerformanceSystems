package ru.itmo.hotdogs.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.feign.FeignConfig;
import ru.itmo.hotdogs.model.dto.UserDto;
import ru.itmo.hotdogs.model.entity.UserEntity;

@FeignClient(name = "user", url = "localhost:8081/users", configuration = FeignConfig.class)
public interface UserApi {
	@PostMapping(path = "/new", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	UserEntity createNewUser(@RequestBody UserDto userDto) throws AlreadyExistsException;

	@GetMapping(path = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
	UserEntity findByLogin(@RequestParam String login) throws NotFoundException;

}
