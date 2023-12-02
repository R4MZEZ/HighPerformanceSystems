package ru.itmo.userservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.userservice.model.dto.JwtRequest;
import ru.itmo.userservice.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
public class AuthController {
	private final AuthService authService;
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody JwtRequest request){
		try {
			return ResponseEntity.ok(authService.createAuthToken(request));
		}catch (BadCredentialsException e){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Логин или пароль неверные");
		}
	}




}
