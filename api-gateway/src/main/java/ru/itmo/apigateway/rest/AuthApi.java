package ru.itmo.apigateway.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.itmo.apigateway.dto.UserDto;

@FeignClient(name = "auth", url = "localhost:8081/users")
public interface AuthApi {
	@PostMapping("/validate")
	ResponseEntity<UserDto> validate(@RequestHeader("Authorization") String bearer);
}
