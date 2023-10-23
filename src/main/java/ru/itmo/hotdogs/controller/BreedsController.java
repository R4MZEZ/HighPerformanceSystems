package ru.itmo.hotdogs.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.service.BreedService;
import ru.itmo.hotdogs.utils.ControllerConfig;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/breeds")
public class BreedsController {

	private final BreedService breedService;

	@GetMapping
	public ResponseEntity<List<BreedEntity>> findAll(@RequestParam(defaultValue = "0") int page) {
		PageRequest pageRequest = PageRequest.of(page, ControllerConfig.pageSize, Sort.by(Sort.Order.asc("id")));
		Page<BreedEntity> entityPage = breedService.findAll(pageRequest);

		return ResponseEntity.ok()
			.header("X-Total-Count", String.valueOf(entityPage.getTotalElements()))
			.body(entityPage.getContent());
	}

	@PostMapping("/new")
	public ResponseEntity<?> addBreed(@RequestBody BreedEntity breed) {
		try {
			breedService.createBreed(breed);
			return ResponseEntity.status(HttpStatus.CREATED).body("Порода успешно создана");
		} catch (AlreadyExistsException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

}