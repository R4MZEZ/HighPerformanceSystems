package ru.itmo.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.itmo.userservice.exceptions.NotFoundException;
import ru.itmo.userservice.model.dto.JwtRequest;
import ru.itmo.userservice.model.dto.JwtResponse;
import ru.itmo.userservice.security.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserService userService;
	private final JwtTokenUtils jwtTokenUtils;
	private final ReactiveAuthenticationManager authenticationManager;


	public Mono<JwtResponse> createAuthToken(JwtRequest request) throws BadCredentialsException, UsernameNotFoundException {
//		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
//		UserDetails userDetails = userService.findByUsername(request.getLogin()).;
//		String token = jwtTokenUtils.generateToken(userDetails);
//		return new JwtResponse(token);

		return userService.findByUsername(request.getLogin())
			.flatMap(userDetails -> {
				if (userDetails == null || !BCrypt.checkpw(request.getPassword(),
					userDetails.getPassword())) {
					return Mono.error(new BadCredentialsException(""));
				}
				authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getLogin(),
						request.getPassword()));
				String token = jwtTokenUtils.generateToken(userDetails);
				return Mono.just(new JwtResponse(token));
			});
	}

}
