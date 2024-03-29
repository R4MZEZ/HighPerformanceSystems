package ru.itmo.userservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.userservice.model.dto.JwtRequest;
import ru.itmo.userservice.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
@Tag(name="Авторизация", description="управляет входом в аккаунт")
public class AuthController {
	private final AuthService authService;
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody JwtRequest request){
		try {
			return ResponseEntity.ok(authService.createAuthToken(request).block());
		}catch (BadCredentialsException | UsernameNotFoundException e){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Логин или пароль неверные");
		}
	}

}
