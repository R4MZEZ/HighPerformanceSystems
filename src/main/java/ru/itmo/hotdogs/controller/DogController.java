package ru.itmo.hotdogs.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.dto.RecommendedDogDto;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.service.DogService;

@RequiredArgsConstructor
@RequestMapping(path = "/dogs")
@RestController
public class DogController {

	private final DogService dogService;

	@PostMapping(path = "/new")
	public ResponseEntity<?> createNewDog(@RequestBody RecommendedDogDto dog) {
		dogService.save(dog);
		return ResponseEntity.ok("Собака успешно создана");
	}

	@PostMapping(path = "/{id}/rate")
	public ResponseEntity<?> likeRecommended(@PathVariable long id,
		@RequestParam boolean is_like) {
		try {
			RecommendedDogDto matchedDog = dogService.rateRecommended(id, is_like);
			if (matchedDog != null) {
				return new ResponseEntity<>(
					"It's a match! With \n%s, %d\n%.1f km away.".formatted(
						matchedDog.getName(),
						matchedDog.getAge(),
						matchedDog.getDistance() / 1000), HttpStatus.FOUND);
			} else {
				return ResponseEntity.ok(getNewRecommendation(id));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
		}
	}

	@GetMapping("/{id}/recommend")
	public ResponseEntity<?> getNewRecommendation(@PathVariable long id) {
		try {
			return ResponseEntity.ok(dogService.findNearest(id));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
		}
	}

	@PatchMapping("/{dogId}/add-interest")
	public ResponseEntity<?> addInterest(@PathVariable long dogId, @RequestParam int id,
		@RequestParam int level) {
		try {
			dogService.addInterest(dogId, id, level);
			return ResponseEntity.ok("Интерес успешно добавлен собаке.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
		}
	}

	@GetMapping()
	public ResponseEntity<List<DogEntity>> findAll() {
		return ResponseEntity.ok(dogService.findAll());
	}

	@GetMapping("{id}")
	public ResponseEntity<?> findById(@PathVariable long id) {
		try {
			return ResponseEntity.ok(dogService.findById(id));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
		}
	}

}
