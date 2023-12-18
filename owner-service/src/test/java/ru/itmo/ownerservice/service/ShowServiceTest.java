package ru.itmo.ownerservice.service;


import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.ownerservice.model.dto.ShowDtoRequest;
import ru.itmo.ownerservice.model.entity.OwnerEntity;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
public class ShowServiceTest {

	@Autowired
	private ShowService showService;


	@ParameterizedTest
	@MethodSource("generateData")
	void invalidCreateShowTest(Timestamp datetime, Long prize, Set<String> allowedBreeds) {
		final var newShowDto = new ShowDtoRequest(datetime, prize, allowedBreeds);

		Assertions.assertThrows(
			ConstraintViolationException.class,
			() -> showService.createShow(new OwnerEntity(), newShowDto));
	}

	static Stream<Arguments> generateData() {
		return Stream.of(
			Arguments.of(new Timestamp(new Date().getTime() + 5000), 500L, Set.of()),
			Arguments.of(new Timestamp(new Date().getTime() - 5000), 500L,
				Set.of("chihuahua", "taksa")),
			Arguments.of(new Timestamp(new Date().getTime() + 5000), -1L, Set.of("taksa"))

		);
	}

//
//	@Test
//	@Transactional
//	void addParticipantTest() throws AlreadyExistsException {
//		var datetime = new Timestamp(new Date().getTime() + 5000);
//		final var newShowDto = new ShowDtoRequest(
//			datetime,
//			500L,
//			Set.of("husky", "taksa"));
//
//		var show = showService.saveShow(ownerService.findByLogin("login").get(), newShowDto);
//
//		var participantDog = new DogDto("sharik",5, "taksa","login",new HashMap<>());
//		var user = new UserDto("sharik_login", "password");
//		dogService.createNewDog(user, participantDog);
//
//
//		var dog = dogService.findByLogin("sharik_login");
//		showService.addParticipant(show, dog);
//		Assertions.assertTrue(show.getParticipants().contains(dog));
//
//
//	}

}
