package ru.itmo.hotdogs.service;

import java.util.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.BreedNotAllowedException;
import ru.itmo.hotdogs.exceptions.CheatingException;
import ru.itmo.hotdogs.exceptions.NotEnoughMoneyException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.ShowDateException;
import ru.itmo.hotdogs.model.dto.NewDogDto;
import ru.itmo.hotdogs.model.dto.NewOwnerDto;
import ru.itmo.hotdogs.model.dto.NewShowDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
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

	@BeforeEach
	void clearData() {
		showService.deleteAll();
		dogService.deleteAll();
		ownerService.deleteAll();
		userService.deleteAll();
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
		NewOwnerDto newOwnerDto = new NewOwnerDto(name, surname, balance, latitude, longitude,
			isOrganizer);
		newOwnerDto.setLogin(login);
		newOwnerDto.setPassword(password);

		Assertions.assertDoesNotThrow(
			() -> Assertions.assertEquals(newOwnerDto, ownerService.createNewOwner(newOwnerDto)));
	}

	@ParameterizedTest
	@CsvSource(value = {
		"elt, 123, Elt0n, John, 1.5f, 1, 1, false",
		"frd, 321, Fred, Nu1l, 0, 1.5, 1.5, false",
		"erc, _+#, Eric, Cartman, -1123.5412f, 0, 0, true"})
	void invalidCreationTest(String login, String password, String name, String surname,
		Float balance, Double latitude,
		Double longitude, Boolean isOrganizer) {
		NewOwnerDto newOwnerDto = new NewOwnerDto(name, surname, balance, latitude, longitude,
			isOrganizer);
		newOwnerDto.setLogin(login);
		newOwnerDto.setPassword(password);

		Assertions.assertThrows(ConstraintViolationException.class,
			() -> ownerService.createNewOwner(newOwnerDto));
	}

	@Test
	void loginAlreadyExistsTest() {
		NewOwnerDto newOwnerDto = new NewOwnerDto("Elton", "John", 1.5f, 1d, 1d, false);
		newOwnerDto.setLogin("login");
		newOwnerDto.setPassword("password");

		Assertions.assertDoesNotThrow(
			() -> Assertions.assertEquals(newOwnerDto, ownerService.createNewOwner(newOwnerDto)));

		Assertions.assertThrows(AlreadyExistsException.class,
			() -> ownerService.createNewOwner(newOwnerDto));

	}

	@BeforeAll
	public void fillBreeds() {
		breedService.createBreed(new BreedEntity("husky"));
		breedService.createBreed(new BreedEntity("taksa"));
		breedService.createBreed(new BreedEntity("labrador"));
	}

	@Test
	void findByLoginTest() throws AlreadyExistsException {
		NewOwnerDto newOwnerDto = new NewOwnerDto("Elton", "John", 500f, 1d, 1d, false);
		newOwnerDto.setLogin("login");
		newOwnerDto.setPassword("password");
		ownerService.createNewOwner(newOwnerDto);

		Assertions.assertDoesNotThrow(() -> {
			OwnerEntity createdOwner = ownerService.findByLogin("login");
			Assertions.assertEquals(newOwnerDto.getName(), createdOwner.getName());
		});


	}

	@Test
	void validCreateShowTest() throws NotFoundException, AlreadyExistsException {
		NewShowDto newShowDto = new NewShowDto(Timestamp.valueOf("2023-11-01 10:00:00"), 500L,
			Set.of("husky", "taksa"));
		NewOwnerDto newOwnerDto = new NewOwnerDto("Elton", "John", 500f, 1d, 1d, false);
		newOwnerDto.setLogin("login");
		newOwnerDto.setPassword("password");
		ownerService.createNewOwner(newOwnerDto);

		Assertions.assertDoesNotThrow(() -> ownerService.createShow("login", newShowDto));

		OwnerEntity owner = ownerService.findByLogin("login");
		Assertions.assertEquals(0, owner.getBalance());
		Assertions.assertEquals(500f, owner.getReservedBalance());
	}

	@Test
	void notEnoughMoneyTest() throws AlreadyExistsException {
		NewShowDto newShowDto = new NewShowDto(Timestamp.valueOf("2023-11-01 10:00:00"), 500L,
			Set.of());
		NewOwnerDto newOwnerDto = new NewOwnerDto("Elton", "John", 0f, 1d, 1d, false);
		newOwnerDto.setLogin("login");
		newOwnerDto.setPassword("password");
		ownerService.createNewOwner(newOwnerDto);

		Assertions.assertThrows(NotEnoughMoneyException.class,
			() -> ownerService.createShow("login", newShowDto));
	}

	@Test
	@Transactional
	void validFinishShowTest()
		throws AlreadyExistsException, NotEnoughMoneyException, NotFoundException, CheatingException, BreedNotAllowedException, ShowDateException {
		NewShowDto showDto = new NewShowDto(Timestamp.valueOf("2023-11-01 10:00:00"), 500L, Set.of("husky", "taksa"));
		NewOwnerDto organizer = new NewOwnerDto("login", "password", "Elton", "John", 500f, 1d, 1d, true);
		ownerService.createNewOwner(organizer);
		ShowEntity show = ownerService.createShow("login", showDto);

		NewOwnerDto participantOwner = new NewOwnerDto("login2", "password", "Ben", "John", 500f, 1d, 1d, false);
		NewDogDto participantDog = new NewDogDto("sharik_login","password","sharik",5, "taksa","login2",new HashMap<>());
		ownerService.createNewOwner(participantOwner);
		dogService.createNewDog(participantDog);

		dogService.applyToShow("sharik_login", show.getId());
		show = showService.findById(show.getId());
		show.setDate(new Timestamp(new Date().getTime()));
		showService.save(show);

		long showId = show.getId();
		Assertions.assertDoesNotThrow(() -> ownerService.finishShow("login", showId,
			dogService.findByLogin("sharik_login").getId()));
	}

}
