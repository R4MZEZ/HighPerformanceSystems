package ru.itmo.hotdogs.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.model.dto.JwtRequest;
import ru.itmo.hotdogs.service.AuthService;

@RestController
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	@PostMapping("/auth")
	public ResponseEntity<?> authorize(@RequestBody JwtRequest request){
		return authService.createAuthToken(request);
	}




}
