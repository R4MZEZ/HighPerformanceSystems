package ru.itmo.hotdogs.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
		try {
			return ResponseEntity.ok(authService.createAuthToken(request));
		}catch (BadCredentialsException e){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Логин и/или пароль неверные");
		}
	}




}
