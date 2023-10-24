package ru.itmo.hotdogs.service;


import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.dto.DogDto;
import ru.itmo.hotdogs.model.dto.OwnerDto;
import ru.itmo.hotdogs.model.dto.ShowDtoRequest;
import ru.itmo.hotdogs.model.dto.UserDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class ShowServiceTest {

	@Autowired
	private OwnerService ownerService;
	@Autowired
	private BreedService breedService;
	@Autowired
	private ShowService showService;
	@Autowired
	private DogService dogService;
	@Autowired
	private UserService userService;

	@AfterEach
	void clearData() {
		showService.deleteAll();
		dogService.deleteAll();
		ownerService.deleteAll();
		userService.deleteAll();
	}

	@BeforeEach
	void createOrganizer() throws AlreadyExistsException {
		ownerService.createNewOwner(
			new UserDto("login", "password"),
			new OwnerDto("Elton", "John", 100000f, 1d, 1d, true));
	}
	@BeforeAll
	void fillBreeds() throws AlreadyExistsException {
		breedService.deleteAll();
		breedService.createBreed(new BreedEntity("husky"));
		breedService.createBreed(new BreedEntity("taksa"));
		breedService.createBreed(new BreedEntity("chihuahua"));
	}

	@AfterAll
	void clearBreeds(){
		breedService.deleteAll();
	}





	@Test
	void createShowTest() throws NotFoundException {
		var datetime = new Timestamp(new Date().getTime() + 5000);
		final var newShowDto = new ShowDtoRequest(
			datetime,
			500L,
			Set.of("husky", "taksa"));

		final var ownerEntity = ownerService.findByLogin("login");

		Assertions.assertDoesNotThrow(() -> {
			var show = showService.createShow(ownerEntity, newShowDto);
			Assertions.assertEquals(datetime, show.getDate());
			Assertions.assertEquals(500L, show.getPrize());
			Assertions.assertEquals(ownerEntity, show.getOrganizer());
			Assertions.assertNull(show.getWinner());
			Assertions.assertEquals(0, show.getParticipants().size());
		});
	}

	@ParameterizedTest
	@MethodSource("generateData")
	void invalidCreateShowTest(Timestamp datetime, Long prize, Set<String> allowedBreeds)
		throws NotFoundException {
		final var ownerEntity = ownerService.findByLogin("login");
		final var newShowDto = new ShowDtoRequest(datetime, prize, allowedBreeds);

		Assertions.assertThrows(
			ConstraintViolationException.class,
			() -> showService.createShow(ownerEntity, newShowDto));
	}

	static Stream<Arguments> generateData() {
		return Stream.of(
			Arguments.of(new Timestamp(new Date().getTime() + 5000), 500L, Set.of()),
			Arguments.of(new Timestamp(new Date().getTime() - 5000), 500L,
				Set.of("chihuahua", "taksa")),
			Arguments.of(new Timestamp(new Date().getTime() + 5000), -1L, Set.of("taksa"))

		);
	}


	@Test
	@Transactional
	void addParticipantTest() throws NotFoundException, AlreadyExistsException {
		var datetime = new Timestamp(new Date().getTime() + 5000);
		final var newShowDto = new ShowDtoRequest(
			datetime,
			500L,
			Set.of("husky", "taksa"));

		var show = showService.createShow(ownerService.findByLogin("login"), newShowDto);

		var participantDog = new DogDto("sharik",5, "taksa","login",new HashMap<>());
		var user = new UserDto("sharik_login", "password");
		dogService.createNewDog(user, participantDog);


		var dog = dogService.findByLogin("sharik_login");
		showService.addParticipant(show, dog);
		Assertions.assertTrue(show.getParticipants().contains(dog));


	}

}
