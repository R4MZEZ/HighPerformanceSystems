package ru.itmo.hotdogs.service;


import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.NullRecommendationException;
import ru.itmo.hotdogs.model.dto.DogInterestDto;
import ru.itmo.hotdogs.model.dto.RecommendedDog;
import ru.itmo.hotdogs.model.dto.RecommendedDogDto;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInteractionsEntity;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.repository.DogRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class DogServiceTest {

	@MockBean
	private InterestService interestService;

	@MockBean
	private DogRepository dogRepository;

	@MockBean
	private DogsInteractionsService dogsInteractionsService;

	@Autowired
	private DogService dogService;

	@Mock
	DogEntity dogMock;

	@Test
	void invalidInterestTest() {
		var dog = new DogEntity();
		var interestName = "hunting";
		var interestLevel = -1;
		var interest = new DogInterestDto(interestName, interestLevel);

		Assertions.assertThrows(ConstraintViolationException.class,
			() -> dogService.addInterest(dog, interest));
	}

	@Test
	void duplicateInterestTest() throws NotFoundException {

//		dogMock = new DogEntity();

		var interestName = "hunting";
		var interestLevel = 5;
		var interestDto = new DogInterestDto(interestName, interestLevel);
		var interest = new InterestEntity(interestName);

		when(interestService.findByName(interestName)).thenReturn(interest);
		when(dogMock.getInterests()).thenReturn(List.of(new DogsInterestsEntity(dogMock, interest, interestLevel)));

		Assertions.assertThrows(AlreadyExistsException.class,
			() -> dogService.addInterest(dogMock, interestDto));
	}



	@Test
	void findNearestTest() {
		double x = 1;
		double y = 2;
		var owner = new OwnerEntity();
		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate coordinate = new Coordinate(x, y);
		owner.setLocation(geometryFactory.createPoint(coordinate));

		var dog = new DogEntity();
		long id = 1L;
		dog.setId(id);
		dog.setOwner(owner);
		when(dogRepository.findNearest(x, y, id)).thenReturn(null);

		Assertions.assertThrows(NotFoundException.class,
			() -> dogService.findNearest(dog));
	}


	@Test
	void rateRecommendedTest() throws NotFoundException {
		double x1 = 1;
		double y1 = 2;
		var sharikOwner = new OwnerEntity();
		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate coordinate = new Coordinate(x1, y1);
		sharikOwner.setLocation(geometryFactory.createPoint(coordinate));

		var sharik = new DogEntity();
		long id1 = 1L;
		sharik.setId(id1);
		sharik.setOwner(sharikOwner);

		double x2 = 3;
		double y2 = 4;
		var bobikOwner = new OwnerEntity();
		coordinate = new Coordinate(x2, y2);
		bobikOwner.setLocation(geometryFactory.createPoint(coordinate));

		var bobik = new DogEntity();
		long id2 = 2L;
		bobik.setId(id2);
		bobik.setOwner(bobikOwner);

		when(dogRepository.findNearest(x1, y1, id1)).thenReturn(new RecommendedDogDto(id2, null, null, null));
		when(dogRepository.findNearest(x2, y2, id2)).thenReturn(new RecommendedDogDto(id1, null, null, null));
		when(dogRepository.findById(id1)).thenReturn(Optional.of(sharik));
		when(dogRepository.findById(id2)).thenReturn(Optional.of(bobik));
		DogsInteractionsEntity interaction = new DogsInteractionsEntity(sharik, bobik, true);
		when(dogsInteractionsService.save(interaction)).thenReturn(interaction);

		dogService.findNearest(sharik);

		Assertions.assertDoesNotThrow(() -> {
			dogService.rateRecommended(sharik, true);
			Assertions.assertEquals(1, sharik.getInteractions().size());
			DogsInteractionsEntity interact = sharik.getInteractions().get(0);
			Assertions.assertEquals(sharik, interact.getSender());
			Assertions.assertEquals(bobik, interact.getReceiver());
			Assertions.assertTrue(interact.getIsLiked());
		});


	}

	@Test
	void matchTest() throws NotFoundException, NullRecommendationException {
		double x1 = 1;
		double y1 = 2;
		var sharikOwner = new OwnerEntity();
		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate coordinate = new Coordinate(x1, y1);
		sharikOwner.setLocation(geometryFactory.createPoint(coordinate));

		var sharik = new DogEntity();
		long id1 = 1L;
		sharik.setId(id1);
		sharik.setOwner(sharikOwner);

		double x2 = 3;
		double y2 = 4;
		var bobikOwner = new OwnerEntity();
		coordinate = new Coordinate(x2, y2);
		bobikOwner.setLocation(geometryFactory.createPoint(coordinate));

		var bobik = new DogEntity();
		long id2 = 2L;
		bobik.setId(id2);
		bobik.setOwner(bobikOwner);

		when(dogRepository.findNearest(x1, y1, id1)).thenReturn(new RecommendedDogDto(id2, null, null, null));
		when(dogRepository.findNearest(x2, y2, id2)).thenReturn(new RecommendedDogDto(id1, null, null, null));
		when(dogRepository.findById(id1)).thenReturn(Optional.of(sharik));
		when(dogRepository.findById(id2)).thenReturn(Optional.of(bobik));
		DogsInteractionsEntity interaction = new DogsInteractionsEntity(sharik, bobik, true);
		when(dogsInteractionsService.save(interaction)).thenReturn(interaction);
		interaction = new DogsInteractionsEntity(bobik, sharik, true);
		when(dogsInteractionsService.save(interaction)).thenReturn(interaction);
		when(dogsInteractionsService.findBySenderAndReceiver(sharik, bobik)).thenReturn(new DogsInteractionsEntity(null, null, true));
		when(dogRepository.findDistance(anyLong(), anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(new RecommendedDogDto(id1, null, null, null));


		dogService.findNearest(sharik);
		dogService.rateRecommended(sharik, true);
		dogService.findNearest(bobik);

		Assertions.assertDoesNotThrow(() -> {
			Optional<RecommendedDog> matched = dogService.rateRecommended(bobik, true);
			Assertions.assertTrue(matched.isPresent());
			Assertions.assertEquals(sharik.getId(), matched.get().getId());
		});


	}
}
