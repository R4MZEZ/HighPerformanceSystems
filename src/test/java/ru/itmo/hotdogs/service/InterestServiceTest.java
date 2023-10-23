package ru.itmo.hotdogs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.entity.InterestEntity;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("test")
class InterestServiceTest {

	@Autowired
	private InterestService interestService;

	@AfterEach
	void clearData(){
		interestService.deleteAll();
	}

	@Test
	void validCreationTest() {
		final var newInterest = interestService.save(new InterestEntity("walking"));
		assertEquals("walking", newInterest.getName());
	}

	@Test
	void invalidNameCreationTest() {
		ConstraintViolationException thrown = Assertions.assertThrows(
			ConstraintViolationException.class, () -> interestService.save(new InterestEntity("walk1ng")));
		Assertions.assertEquals("name: должно соответствовать \"^[a-zA-Z]+$\"",
			thrown.getMessage());
	}

	@Test
	void blankNameCreationTest() {
		ConstraintViolationException thrown = Assertions.assertThrows(
			ConstraintViolationException.class, () -> interestService.save(new InterestEntity("")));
		Assertions.assertTrue(thrown.getMessage().contains("name: не должно быть пустым"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"sleeping", "training", "singing"})
	void findByNameTest(String name) {
		interestService.save(new InterestEntity(name));
		Assertions.assertDoesNotThrow(() -> {
			InterestEntity interest = interestService.findByName(name);
			assertEquals(name, interest.getName());
		});

	}

	@Test
	void findByInvalidNameTest() {
		NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> interestService.findByName("hmmm"));
		Assertions.assertEquals("Интереса с названием hmmm не существует", thrown.getMessage());
	}


}