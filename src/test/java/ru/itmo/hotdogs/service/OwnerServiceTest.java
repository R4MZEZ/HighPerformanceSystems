package ru.itmo.hotdogs.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.BreedNotAllowedException;
import ru.itmo.hotdogs.exceptions.CheatingException;
import ru.itmo.hotdogs.exceptions.NotEnoughMoneyException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.ShowDateException;
import ru.itmo.hotdogs.model.dto.DogDto;
import ru.itmo.hotdogs.model.dto.OwnerDto;
import ru.itmo.hotdogs.model.dto.ShowDtoRequest;
import ru.itmo.hotdogs.model.dto.UserDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class OwnerServiceTest {

	@Autowired
	private OwnerService ownerService;
	@Autowired
	private BreedService breedService;
	@Autowired
	private UserService userService;
	@Autowired
	private ShowService showService;
	@Autowired
	private DogService dogService;

	@AfterEach
	void clearData() {
		showService.deleteAll();
		dogService.deleteAll();
		ownerService.deleteAll();
		userService.deleteAll();
	}

	@BeforeAll
	public void fillBreeds() throws AlreadyExistsException {
		breedService.deleteAll();
		breedService.createBreed(new BreedEntity("husky"));
		breedService.createBreed(new BreedEntity("taksa"));
		breedService.createBreed(new BreedEntity("labrador"));
	}

	@AfterAll
	public void clearBreeds(){
		breedService.deleteAll();
	}

	@ParameterizedTest
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
	void loginAlreadyExistsTest() {
		OwnerDto ownerDto = new OwnerDto("Elton", "John", 1.5f, 1d, 1d, false);
		UserDto userDto = new UserDto("login", "password");


		Assertions.assertDoesNotThrow(
			() -> Assertions.assertEquals(
				ownerDto.getName(), ownerService.createNewOwner(userDto, ownerDto).getName()));

		Assertions.assertThrows(AlreadyExistsException.class,
			() -> ownerService.createNewOwner(userDto, ownerDto));

	}



	@Test
	void findByLoginTest() throws AlreadyExistsException {
		OwnerDto ownerDto = new OwnerDto("Elton", "John", 500f, 1d, 1d, false);
		UserDto userDto = new UserDto("login", "password");

		ownerService.createNewOwner(userDto, ownerDto);

		Assertions.assertDoesNotThrow(() -> {
			OwnerEntity createdOwner = ownerService.findByLogin("login").get();
			Assertions.assertEquals(ownerDto.getName(), createdOwner.getName());
		});


	}

	@Test
	void validCreateShowTest() throws NotFoundException, AlreadyExistsException {
		ShowDtoRequest newShowDto = new ShowDtoRequest(Timestamp.valueOf("2024-11-01 10:00:00"), 500L,
			Set.of("husky", "taksa"));
		OwnerDto ownerDto = new OwnerDto("Elton", "John", 500f, 1d, 1d, false);
		UserDto userDto = new UserDto("login", "password");

		ownerService.createNewOwner(userDto, ownerDto);

		Assertions.assertDoesNotThrow(() -> ownerService.createShow("login", newShowDto));

		OwnerEntity owner = ownerService.findByLogin("login").get();
		Assertions.assertEquals(0, owner.getBalance());
		Assertions.assertEquals(500f, owner.getReservedBalance());
	}

	@Test
	void notEnoughMoneyTest() throws AlreadyExistsException {
		ShowDtoRequest newShowDto = new ShowDtoRequest(Timestamp.valueOf("2023-11-01 10:00:00"), 500L,
			Set.of());
		OwnerDto ownerDto = new OwnerDto("Elton", "John", 0f, 1d, 1d, false);
		UserDto userDto = new UserDto("login", "password");

		ownerService.createNewOwner(userDto, ownerDto);

		Assertions.assertThrows(NotEnoughMoneyException.class,
			() -> ownerService.createShow("login", newShowDto));
	}

	@Test
	void validFinishShowTest()
		throws AlreadyExistsException, NotEnoughMoneyException, NotFoundException, CheatingException, BreedNotAllowedException, ShowDateException {
		ShowDtoRequest showDto = new ShowDtoRequest(Timestamp.valueOf("2024-11-01 10:00:00"), 500L, Set.of("husky", "taksa"));
		OwnerDto organizer = new OwnerDto("Elton", "John", 500f, 1d, 1d, true);
		UserDto user = new UserDto("elton_login", "password");

		ownerService.createNewOwner(user, organizer);
		ShowEntity show = ownerService.createShow("elton_login", showDto);

		OwnerDto participantOwner = new OwnerDto("Ben", "John", 500f, 1d, 1d, false);
		user.setLogin("ben_login");
		ownerService.createNewOwner(user, participantOwner);
		DogDto participantDog = new DogDto("sharik",5, "taksa","ben_login",new HashMap<>());
		user.setLogin("sharik_login");
		DogEntity participantEntity = dogService.createNewDog(user, participantDog);

		dogService.applyToShow(participantEntity, show.getId());
		show = showService.findById(show.getId());
		show.setDate(new Timestamp(new Date().getTime()));
		showService.save(show);

		long showId = show.getId();
		Assertions.assertDoesNotThrow(() -> ownerService.finishShow("elton_login", showId,
			dogService.findByLogin("sharik_login").getId()));
	}

}
