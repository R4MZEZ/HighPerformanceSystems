package ru.itmo.hotdogs.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testcontainers.junit.jupiter.Testcontainers;
//import ru.itmo.hotdogs.security.JwtTokenUtils;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
public class AuthServiceTest {

//	@Autowired
//	private UserService userService;
//	@Autowired
//	private AuthService authService;
//	@Autowired
//	private JwtTokenUtils jwtTokenUtils;

//	@AfterEach
//	void clearUsers() {
//		userService.deleteAll();
//	}

//	@Test
//	void validAuthTest() {
//		String login = "login";
//		String password = "password";
//		Assertions.assertDoesNotThrow(
//			() -> userService.createNewUser(new UserDto(login, password, Set.of("ROLE_DOG"))));
//		JwtResponse jwtResponse = authService.createAuthToken(new JwtRequest(login, password));
//		Assertions.assertEquals(login, jwtTokenUtils.getUsername(jwtResponse.getToken()));
//		Assertions.assertEquals(List.of("ROLE_DOG"),
//			jwtTokenUtils.getRoles(jwtResponse.getToken()));
//	}

//	@ParameterizedTest
//	@CsvSource({
//		"login, password, login, wrongPassword",
//		"login, password, wrongLogin, password",
//		"login, password, wrongLogin, wrongPassword"
//	})
//	void invalidPasswordAuthTest(String regLogin, String regPassword, String authLogin,
//		String authPassword) {
//		Assertions.assertDoesNotThrow(() ->
//			userService.createNewUser(new UserDto(regLogin, regPassword, Set.of("ROLE_DOG"))));
//		Assertions.assertThrows(BadCredentialsException.class,
//			() -> authService.createAuthToken(new JwtRequest(authLogin, authPassword)));
//	}

}
