package ru.itmo.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.itmo.userservice.model.dto.JwtRequest;
import ru.itmo.userservice.model.dto.JwtResponse;
import ru.itmo.userservice.security.JwtTokenUtils;

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
