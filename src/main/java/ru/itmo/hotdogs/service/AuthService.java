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

	public JwtResponse createAuthToken(JwtRequest request) throws BadCredentialsException{
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
		UserDetails userDetails = userService.loadUserByUsername(request.getLogin());
		String token = jwtTokenUtils.generateToken(userDetails);
		return new JwtResponse(token);
	}
}
