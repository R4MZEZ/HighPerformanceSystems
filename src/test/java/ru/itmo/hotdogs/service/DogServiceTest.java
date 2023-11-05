package ru.itmo.hotdogs.service;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.NullRecommendationException;
import ru.itmo.hotdogs.model.dto.DogDto;
import ru.itmo.hotdogs.model.dto.DogInterestDto;
import ru.itmo.hotdogs.model.dto.OwnerDto;
import ru.itmo.hotdogs.model.dto.ShowDtoRequest;
import ru.itmo.hotdogs.model.dto.UserDto;
import ru.itmo.hotdogs.model.dto.RecommendedDogDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInteractionsEntity;
import ru.itmo.hotdogs.model.entity.InterestEntity;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class DogServiceTest {

	@Autowired
	private DogService dogService;
	@Autowired
	private InterestService interestService;
	@Autowired
	private OwnerService ownerService;
	@Autowired
	private UserService userService;
	@Autowired
	private BreedService breedService;
	@Autowired
	private ShowService showService;

	@BeforeAll
	void fillData() throws AlreadyExistsException {
		interestService.save(new InterestEntity("walking"));
		interestService.save(new InterestEntity("sleeping"));
		interestService.save(new InterestEntity("hunting"));
		breedService.deleteAll();
		breedService.createBreed(new BreedEntity("husky"));
		breedService.createBreed(new BreedEntity("taksa"));
		breedService.createBreed(new BreedEntity("labrador"));
	}

	@AfterAll
	void clearInterests() {
		interestService.deleteAll();
		breedService.deleteAll();
	}

	@AfterEach
	void clearData() {
		showService.deleteAll();
		dogService.deleteAll();
		ownerService.deleteAll();
		userService.deleteAll();
	}

	@Test
	void addInterestTest() throws AlreadyExistsException, NotFoundException {
		ownerService.createNewOwner(
			new UserDto("login", "password"),
			new OwnerDto("Elton", "John", 100000f, 1d, 1d, true));
		var participantDog = new DogDto("sharik", 5, "taksa", "login", Map.of());
		var user = new UserDto("sharik_login", "password");

		DogEntity dog = dogService.createNewDog(user, participantDog);

		var interestName = "hunting";
		var interestLevel = 5;
		var interest = new DogInterestDto(interestName, interestLevel);

		Assertions.assertDoesNotThrow(() -> {
			DogEntity updated = dogService.addInterest(dog, interest);
			Assertions.assertEquals(1, updated.getInterests().size());
			Assertions.assertEquals(interestName,
				updated.getInterests().get(0).getInterest().getName());
			Assertions.assertEquals(interestLevel, updated.getInterests().get(0).getLevel());
		});
	}

	@Test
	void invalidInterestTest() throws AlreadyExistsException, NotFoundException {
		ownerService.createNewOwner(
			new UserDto("login", "password"),
			new OwnerDto("Elton", "John", 100000f, 1d, 1d, true));
		var participantDog = new DogDto("sharik", 5, "taksa", "login", new HashMap<>());
		var user = new UserDto("sharik_login", "password");
		var dog = dogService.createNewDog(user, participantDog);

		var interestName = "hunting";
		var interestLevel = -1;
		var interest = new DogInterestDto(interestName, interestLevel);

		Assertions.assertThrows(ConstraintViolationException.class,
			() -> dogService.addInterest(dog, interest));
	}

	@Test
	void duplicateInterestTest() throws AlreadyExistsException, NotFoundException {
		ownerService.createNewOwner(
			new UserDto("login", "password"),
			new OwnerDto("Elton", "John", 100000f, 1d, 1d, true));
		var participantDog = new DogDto("sharik", 5, "taksa", "login", new HashMap<>());
		var user = new UserDto("sharik_login", "password");
		var dog = dogService.createNewDog(user, participantDog);

		var interestName = "hunting";
		var interestLevel = 5;
		var interest = new DogInterestDto(interestName, interestLevel);

		Assertions.assertDoesNotThrow(
			() -> dogService.addInterest(dog, interest));

		Assertions.assertThrows(AlreadyExistsException.class,
			() -> dogService.addInterest(dog, interest));
	}


	@Test
	void applyToShowTest() throws AlreadyExistsException, NotFoundException {
		ownerService.createNewOwner(
			new UserDto("elton_login", "password"),
			new OwnerDto("Elton", "John", 100000f, 1d, 1d, true));

		var datetime = Timestamp.valueOf("2024-11-01 10:00:00");
		var newShowDto = new ShowDtoRequest(datetime, 500L, Set.of("husky", "taksa"));

		var organizerEntity = ownerService.findByLogin("elton_login");
		var show = showService.createShow(organizerEntity.get(), newShowDto);

		ownerService.createNewOwner(
			new UserDto("bob_login", "password"),
			new OwnerDto("Bob", "Marley", 0f, 1d, 1d, true));

		var participantDog = new DogDto("sharik", 5, "taksa", "bob_login", new HashMap<>());
		var user = new UserDto("sharik_login", "password");
		var dog = dogService.createNewDog(user, participantDog);

		Assertions.assertDoesNotThrow(() -> {
			var updatedShow = dogService.applyToShow(dog, show.getId());
			Assertions.assertEquals(1, updatedShow.getParticipants().size());
			Assertions.assertTrue(updatedShow.getParticipants().contains(dog));
		});

	}

	@Test
	void findNearestTest() throws AlreadyExistsException, NotFoundException {
		ownerService.createNewOwner(
			new UserDto("elton_login", "password"),
			new OwnerDto("Elton", "John", 100000f, 30.308057, 59.957478, false));
		var eltonDog = new DogDto("eltonik", 5, "taksa", "elton_login", new HashMap<>());
		var user = new UserDto("eltonik_login", "password");
		var eltonikEntity = dogService.createNewDog(user, eltonDog);

		ownerService.createNewOwner(
			new UserDto("bob_login", "password"),
			new OwnerDto("Bob", "Marley", 0f, 30.319051, 59.956323, false));
		var bobDog = new DogDto("bobik", 5, "taksa", "bob_login", new HashMap<>());
		var user1 = new UserDto("bobik_login", "password");
		var bobikEntity = dogService.createNewDog(user1, bobDog);

		ownerService.createNewOwner(
			new UserDto("shar_login", "password"),
			new OwnerDto("Shar", "Ik", 0f, 30.322810, 59.955492, false));
		var sharDog = new DogDto("sharik", 5, "taksa", "shar_login", new HashMap<>());
		var user2 = new UserDto("sharik_login", "password");
		var sharikEntity = dogService.createNewDog(user2, sharDog);


		Assertions.assertDoesNotThrow(() -> {
			RecommendedDogDto nearestToEltonik = dogService.findNearest(eltonikEntity);
			Assertions.assertEquals(bobikEntity.getName(), nearestToEltonik.getName());
			Assertions.assertTrue(Math.abs(625 - nearestToEltonik.getDistance()) < 10);

			RecommendedDogDto nearestToBobik = dogService.findNearest(bobikEntity);
			Assertions.assertEquals(sharikEntity.getName(), nearestToBobik.getName());
			Assertions.assertTrue(Math.abs(229 - nearestToBobik.getDistance()) < 10);

			Assertions.assertEquals(bobikEntity, eltonikEntity.getCurRecommended());
		});

	}


	@Test
	void rateRecommendedTest() throws AlreadyExistsException, NotFoundException {
		ownerService.createNewOwner(
			new UserDto("elton_login", "password"),
			new OwnerDto("Elton", "John", 100000f, 30.308057, 59.957478, false));
		var eltonDog = new DogDto("eltonik", 5, "taksa", "elton_login", new HashMap<>());
		var user = new UserDto("eltonik_login", "password");
		var eltonikEntity = dogService.createNewDog(user, eltonDog);

		ownerService.createNewOwner(
			new UserDto("bob_login", "password"),
			new OwnerDto("Bob", "Marley", 0f, 30.319051, 59.956323, false));
		var bobDog = new DogDto("bobik", 5, "taksa", "bob_login", new HashMap<>());
		var user1 = new UserDto("bobik_login", "password");
		var bobikEntity = dogService.createNewDog(user1, bobDog);

		dogService.findNearest(eltonikEntity);
		Assertions.assertDoesNotThrow(() -> {
			Assertions.assertTrue(dogService.rateRecommended(eltonikEntity, true).isEmpty());
			Assertions.assertEquals(1, eltonikEntity.getInteractions().size());
			DogsInteractionsEntity interaction = eltonikEntity.getInteractions().stream().toList().get(0);
			Assertions.assertEquals(eltonikEntity, interaction.getSender());
			Assertions.assertEquals(bobikEntity, interaction.getReceiver());
			Assertions.assertTrue(interaction.getIsLiked());
		});


	}

	@Test
	void matchTest() throws AlreadyExistsException, NotFoundException, NullRecommendationException {
		ownerService.createNewOwner(
			new UserDto("elton_login", "password"),
			new OwnerDto("Elton", "John", 100000f, 30.308057, 59.957478, false));
		var eltonDog = new DogDto("eltonik", 5, "taksa", "elton_login", new HashMap<>());
		var user = new UserDto("eltonik_login", "password");
		var eltonikEntity = dogService.createNewDog(user, eltonDog);

		ownerService.createNewOwner(
			new UserDto("bob_login", "password"),
			new OwnerDto("Bob", "Marley", 0f, 30.319051, 59.956323, false));
		var bobDog = new DogDto("bobik", 5, "taksa", "bob_login", new HashMap<>());
		var user1 = new UserDto("bobik_login", "password");
		var bobikEntity = dogService.createNewDog(user1, bobDog);

		dogService.findNearest(eltonikEntity);
		dogService.rateRecommended(eltonikEntity, true);

		dogService.findNearest(bobikEntity);

		Assertions.assertDoesNotThrow(() -> {
			Optional<RecommendedDogDto> matched = dogService.rateRecommended(bobikEntity, true);
			Assertions.assertTrue(matched.isPresent());
			Assertions.assertEquals(eltonikEntity.getName(), matched.get().getName());
			Assertions.assertEquals(eltonikEntity.getAge(), matched.get().getAge());
			Assertions.assertTrue(Math.abs(625 - matched.get().getDistance()) < 10);
			Assertions.assertEquals(1, bobikEntity.getMatches().size());
			DogEntity inverse_match = bobikEntity.getMatches().stream().toList().get(0);
			Assertions.assertEquals(eltonikEntity, inverse_match);
		});


	}
}
