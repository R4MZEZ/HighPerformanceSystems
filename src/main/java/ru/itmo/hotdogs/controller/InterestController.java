package ru.itmo.hotdogs.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.service.InterestService;


@RequiredArgsConstructor
@RequestMapping(path = "/interests")
@RestController
public class InterestController {

	private final InterestService interestService;

	@GetMapping
	public ResponseEntity<List<InterestEntity>> findAll() {
		return ResponseEntity.ok(interestService.findAll());
	}

	@PostMapping("/new")
	public ResponseEntity<?> addInterest(@RequestBody InterestEntity interest) {
		interestService.save(interest);
		return ResponseEntity.ok("Интерес успешно создан");
	}
}