package ru.itmo.hotdogs.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.service.BreedService;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("test")
class BreedsControllerTest {

	@Autowired
	private BreedService breedService;

	@Test
	void shouldCreateOnePerson() {
		final var newBreed = breedService.createBreed(new BreedEntity("husky"));
		assertEquals("husky", newBreed.getName());
		assertEquals(1, newBreed.getId());
	}

}