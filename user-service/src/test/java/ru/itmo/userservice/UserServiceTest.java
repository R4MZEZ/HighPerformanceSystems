package ru.itmo.userservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;
import ru.itmo.userservice.exceptions.NotFoundException;
import ru.itmo.userservice.model.dto.UserDto;
import ru.itmo.userservice.model.entity.UserEntity;
import ru.itmo.userservice.service.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
public class UserServiceTest {

	@Autowired
	private UserService userService;


	@ParameterizedTest
	@MethodSource("generateData")
	void invalidCreationUserTest(String login, String password, Set<Integer> roles) {
		StepVerifier.create(userService.createNewUser(new UserDto(login, password, roles)))
			.expectError(ConstraintViolationException.class)
			.verify();
	}

	static Stream<Arguments> generateData() {
		return Stream.of(
			Arguments.of("", "password", Set.of(2)),
			Arguments.of("login", "", Set.of(2)),
			Arguments.of("login", "password", Set.of()),
			Arguments.of("", "", Set.of())
		);
	}

}
