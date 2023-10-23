package ru.itmo.hotdogs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.entity.BreedEntity;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("test")
class BreedsServiceTest {

	@Autowired
	private BreedService breedService;

	@AfterEach
	void clearBreeds(){
		breedService.deleteAll();
	}

	@Test
	void validCreationTest() throws AlreadyExistsException {
		final var newBreed = breedService.createBreed(new BreedEntity("husky"));
		assertEquals("husky", newBreed.getName());
	}

	@Test
	void invalidNameCreationTest() {
		ConstraintViolationException thrown = Assertions.assertThrows(
			ConstraintViolationException.class, () -> breedService.createBreed(new BreedEntity("hu$ky")));
		Assertions.assertEquals("name: должно соответствовать \"^[a-zA-Z]+$\"",
			thrown.getMessage());
	}

	@Test
	void blankNameCreationTest() {
		ConstraintViolationException thrown = Assertions.assertThrows(
			ConstraintViolationException.class, () -> breedService.createBreed(new BreedEntity("")));
		Assertions.assertTrue(thrown.getMessage().contains("name: не должно быть пустым"));
	}

	@ParameterizedTest
	@ValueSource( strings = {"Ovcharka", "Alabai", "Taksa"})
	void findByNameTest(String name) throws AlreadyExistsException {
		breedService.createBreed(new BreedEntity(name));
		Assertions.assertDoesNotThrow(() -> {
			BreedEntity breed = breedService.findByName(name);
			assertEquals(name, breed.getName());
		});

	}

	@Test
	void findByInvalidNameTest() {
		NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> breedService.findByName("hmmm"));
		Assertions.assertEquals("Породы с таким названием не существует", thrown.getMessage());
	}

}