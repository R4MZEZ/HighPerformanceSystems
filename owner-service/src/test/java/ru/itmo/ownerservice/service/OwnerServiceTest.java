package ru.itmo.ownerservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import ru.itmo.ownerservice.exceptions.AlreadyExistsException;
import ru.itmo.ownerservice.exceptions.NotEnoughMoneyException;
import ru.itmo.ownerservice.model.dto.OwnerDto;
import ru.itmo.ownerservice.model.dto.ResponseDto;
import ru.itmo.ownerservice.model.dto.ShowDtoRequest;
import ru.itmo.ownerservice.model.dto.UserDto;
import ru.itmo.ownerservice.model.entity.OwnerEntity;
import ru.itmo.ownerservice.model.entity.UserEntity;
import ru.itmo.ownerservice.repository.OwnerRepository;
import ru.itmo.ownerservice.rest.UserApi;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class OwnerServiceTest {

	@Autowired
	private OwnerService ownerService;
	@MockBean
	private UserApi userApi;
	@MockBean
	private OwnerRepository ownerRepository;

//	@ParameterizedTest
	@CsvSource(value = {
		"elt, 123, Elton, John, 1.5f, 1, 1, false",
		"frd, 321, Fred, null, 0, 1.5, 1.5, false",
		"erc, _+#, Eric, Cartman, 1123.5412f, 0, 0, true",
		"stn, |!@, Stan,  , 1.5f, -1, -1, false"},
		nullValues = {"null"})
	void validCreationTest(String login, String password, String name, String surname,
		Float balance, Double latitude,
		Double longitude, Boolean isOrganizer) {
		UserDto userDto = new UserDto(login, password);
		OwnerDto ownerDto = new OwnerDto(name, surname, balance, latitude, longitude, isOrganizer);

		Assertions.assertDoesNotThrow(
			() -> Assertions.assertEquals(
				ownerDto.getName(), ownerService.createNewOwner(userDto, ownerDto).getName()));
	}

	@ParameterizedTest
	@CsvSource(value = {
		"elt, 123, Elt0n, John, 1.5f, 1, 1, false",
		"frd, 321, Fred, Nu1l, 0, 1.5, 1.5, false",
		"erc, _+#, Eric, Cartman, -1123.5412f, 0, 0, true"})
	void invalidCreationTest(String login, String password, String name, String surname,
		Float balance, Double latitude,
		Double longitude, Boolean isOrganizer) {

		UserDto userDto = new UserDto(login, password);
		OwnerDto ownerDto = new OwnerDto(name, surname, balance, latitude, longitude, isOrganizer);

		Assertions.assertThrows(ConstraintViolationException.class,
			() -> ownerService.createNewOwner(userDto, ownerDto));
	}

	@Test
	void loginAlreadyExistsTest() throws AlreadyExistsException {
		OwnerDto ownerDto = new OwnerDto("Elton", "John", 1.5f, 1d, 1d, false);
		UserDto userDto = new UserDto("login", "password");

		when(userApi.createNewUser(any(UserDto.class))).thenThrow(new AlreadyExistsException(""));

		Assertions.assertThrows(AlreadyExistsException.class,
			() -> ownerService.createNewOwner(userDto, ownerDto));

	}



	@Test
	void findByLoginTest() {
		String name = "Elton";
		OwnerDto ownerDto = new OwnerDto(name, "John", 500f, 1d, 1d, false);

		when(userApi.findByLogin(anyString())).thenReturn(Mono.just(new ResponseDto<>(new UserEntity(), null, HttpStatus.OK)));
		OwnerEntity owner = new OwnerEntity();
		owner.setName(name);
		when(ownerRepository.findByUser(any(UserEntity.class))).thenReturn(Optional.of(owner));

		Assertions.assertDoesNotThrow(() -> {
			OwnerEntity createdOwner = ownerService.findByLogin("login").get();
			Assertions.assertEquals(ownerDto.getName(), createdOwner.getName());
		});


	}


	@Test
	void notEnoughMoneyTest() throws ExecutionException, InterruptedException {
		ShowDtoRequest newShowDto = new ShowDtoRequest(Timestamp.valueOf("2023-11-01 10:00:00"), 500L,
			Set.of());
		OwnerEntity owner = new OwnerEntity();
		owner.setBalance(1f);
		when(userApi.findByLogin(anyString())).thenReturn(Mono.just(new ResponseDto<>(new UserEntity(), null, HttpStatus.OK)));
		when(ownerService.findByLogin(anyString())).thenReturn(Optional.of(owner));


		Assertions.assertThrows(NotEnoughMoneyException.class,
			() -> ownerService.createShow("login", newShowDto));
	}



}
