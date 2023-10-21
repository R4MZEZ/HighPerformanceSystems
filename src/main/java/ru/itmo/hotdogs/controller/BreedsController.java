package ru.itmo.hotdogs.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.repository.BreedRepository;
import ru.itmo.hotdogs.service.BreedService;
import ru.itmo.hotdogs.utils.ControllerConfig;


@RequiredArgsConstructor
@RestController
public class BreedsController {

	private final BreedService breedService;

	@GetMapping("/breeds")
	public ResponseEntity<List<BreedEntity>> findAll(@RequestParam(defaultValue = "0") int page) {
		PageRequest pageRequest = PageRequest.of(page, ControllerConfig.pageSize, Sort.by(Sort.Order.asc("id")));
		Page<BreedEntity> entityPage = breedService.findAll(pageRequest);

		return ResponseEntity.ok()
			.header("X-Total-Count", String.valueOf(entityPage.getTotalElements()))
			.body(entityPage.getContent());
	}

}