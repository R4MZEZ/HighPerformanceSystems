package ru.itmo.hotdogs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.model.dto.JwtRequest;
import ru.itmo.hotdogs.model.dto.JwtResponse;
import ru.itmo.hotdogs.utils.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserService userService;
	private final JwtTokenUtils jwtTokenUtils;
	private final AuthenticationManager authenticationManager;

	public ResponseEntity<?> createAuthToken(JwtRequest request){
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
		}catch (BadCredentialsException e){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Вы не авторизованы");
		}
		UserDetails userDetails = userService.loadUserByUsername(request.getLogin());
		String token = jwtTokenUtils.generateToken(userDetails);
		return ResponseEntity.ok(new JwtResponse(token));
	}
}
