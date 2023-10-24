package ru.itmo.hotdogs.service;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hotdogs.model.dto.JwtRequest;
import ru.itmo.hotdogs.model.dto.JwtResponse;
import ru.itmo.hotdogs.model.dto.NewUserDto;
import ru.itmo.hotdogs.security.JwtTokenUtils;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("test")
public class AuthServiceTest {

	@Autowired
	private UserService userService;
	@Autowired
	private AuthService authService;
	@Autowired
	private JwtTokenUtils jwtTokenUtils;

	@AfterEach
	void clearUsers() {
		userService.deleteAll();
	}

	@Test
	void validAuthTest() {
		String login = "login";
		String password = "password";
		Assertions.assertDoesNotThrow(
			() -> userService.createNewUser(new NewUserDto(login, password, Set.of("ROLE_DOG"))));
		JwtResponse jwtResponse = authService.createAuthToken(new JwtRequest(login, password));
		Assertions.assertEquals(login, jwtTokenUtils.getUsername(jwtResponse.getToken()));
		Assertions.assertEquals(List.of("ROLE_DOG"),
			jwtTokenUtils.getRoles(jwtResponse.getToken()));
	}

	@ParameterizedTest
	@CsvSource({
		"login, password, login, wrongPassword",
		"login, password, wrongLogin, password",
		"login, password, wrongLogin, wrongPassword"
	})
	void invalidPasswordAuthTest(String regLogin, String regPassword, String authLogin,
		String authPassword) {
		Assertions.assertDoesNotThrow(() ->
			userService.createNewUser(new NewUserDto(regLogin, regPassword, Set.of("ROLE_DOG"))));
		Assertions.assertThrows(BadCredentialsException.class,
			() -> authService.createAuthToken(new JwtRequest(authLogin, authPassword)));
	}

}
