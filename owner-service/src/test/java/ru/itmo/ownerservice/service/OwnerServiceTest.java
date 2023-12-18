package ru.itmo.ownerservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;
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
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.ownerservice.exceptions.AlreadyExistsException;
import ru.itmo.ownerservice.exceptions.NotEnoughMoneyException;
import ru.itmo.ownerservice.exceptions.NotFoundException;
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
//	@CsvSource(value = {
//		"elt, 123, Elton, John, 1.5f, 1, 1, false",
//		"frd, 321, Fred, null, 0, 1.5, 1.5, false",
//		"erc, _+#, Eric, Cartman, 1123.5412f, 0, 0, true",
//		"stn, |!@, Stan,  , 1.5f, -1, -1, false"},
//		nullValues = {"null"})
//	void validCreationTest(String login, String password, String name, String surname,
//		Float balance, Double latitude,
//		Double longitude, Boolean isOrganizer) {
//		UserDto userDto = new UserDto(login, password);
//		OwnerDto ownerDto = new OwnerDto(name, surname, balance, latitude, longitude, isOrganizer);
//
//		Assertions.assertDoesNotThrow(
//			() -> Assertions.assertEquals(
//				ownerDto.getName(), ownerService.createNewOwner(userDto, ownerDto).getName()));
//	}

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
	void findByLoginTest() throws NotFoundException {
		String name = "Elton";
		OwnerDto ownerDto = new OwnerDto(name, "John", 500f, 1d, 1d, false);
//		UserDto userDto = new UserDto("login", "password");

		when(userApi.findByLogin(anyString())).thenReturn(new ResponseDto<>(new UserEntity(), null, null));
		OwnerEntity owner = new OwnerEntity();
		owner.setName(name);
		when(ownerRepository.findByUser(any(UserEntity.class))).thenReturn(Optional.of(owner));

		Assertions.assertDoesNotThrow(() -> {
			OwnerEntity createdOwner = ownerService.findByLogin("login").get();
			Assertions.assertEquals(ownerDto.getName(), createdOwner.getName());
		});


	}

//	@Test
//	void validCreateShowTest() throws NotFoundException {
//		when(breedsApi.findBreedByName(anyString())).thenReturn(new BreedEntity());
//		OwnerEntity owner = new OwnerEntity();
//		owner.setBalance(500f);
//		when(ownerService.findByLogin(anyString())).thenReturn(Optional.of(owner));
//		when(showService.saveShow(any(ShowEntity.class))).thenAnswer(i -> i.getArguments()[0]);
//
//		ShowDtoRequest newShowDto = new ShowDtoRequest(Timestamp.valueOf("2024-11-01 10:00:00"), 500L,
//			Set.of("husky", "taksa"));
//
//
//		Assertions.assertDoesNotThrow(() -> {
//			ShowEntity show = ownerService.createShow("login", newShowDto);
//			Assertions.assertEquals(0, show.getOrganizer().getBalance());
//			Assertions.assertEquals(500f, show.getOrganizer().getReservedBalance());
//		});
//
//	}

	@Test
	void notEnoughMoneyTest() {
		ShowDtoRequest newShowDto = new ShowDtoRequest(Timestamp.valueOf("2023-11-01 10:00:00"), 500L,
			Set.of());
		OwnerEntity owner = new OwnerEntity();
		owner.setBalance(1f);
		when(ownerService.findByLogin(anyString())).thenReturn(Optional.of(owner));

		Assertions.assertThrows(NotEnoughMoneyException.class,
			() -> ownerService.createShow("login", newShowDto));
	}

//	@Test
//	void validFinishShowTest()
//		throws AlreadyExistsException, NotEnoughMoneyException, NotFoundException, CheatingException, BreedNotAllowedException, ShowDateException {
//		ShowDtoRequest showDto = new ShowDtoRequest(Timestamp.valueOf("2024-11-01 10:00:00"), 500L, Set.of("husky", "taksa"));
//		OwnerDto organizer = new OwnerDto("Elton", "John", 500f, 1d, 1d, true);
//		UserDto user = new UserDto("elton_login", "password");
//
//		ownerService.createNewOwner(user, organizer);
//		ShowEntity show = ownerService.saveShow("elton_login", showDto);
//
//		OwnerDto participantOwner = new OwnerDto("Ben", "John", 500f, 1d, 1d, false);
//		user.setLogin("ben_login");
//		ownerService.createNewOwner(user, participantOwner);
//		DogDto participantDog = new DogDto("sharik",5, "taksa","ben_login",new HashMap<>());
//		user.setLogin("sharik_login");
//		DogEntity participantEntity = dogService.createNewDog(user, participantDog);
//
//		dogService.applyToShow(participantEntity, show.getId());
//		show = showService.findById(show.getId());
//		show.setDate(new Timestamp(new Date().getTime()));
//		showService.createInterest(show);
//
//		long showId = show.getId();
//		Assertions.assertDoesNotThrow(() -> ownerService.finishShow("elton_login", showId,
//			dogService.findByLogin("sharik_login").getId()));
//	}

}
