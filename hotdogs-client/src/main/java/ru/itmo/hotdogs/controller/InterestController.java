package ru.itmo.hotdogs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.service.InterestService;


@RequiredArgsConstructor
@RequestMapping(path = "/dogs/interests")
@RestController
@Tag(name = "Реестр интересов", description = "Интересы-занятия, отображающиеся в странице собаки")
public class InterestController {

	private final InterestService interestService;

	@GetMapping
	@Operation(summary = "Получение всех интересов", description = "Вывести список всех интересов")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<List<InterestEntity>> findAll() {
		return ResponseEntity.ok(interestService.findAll());
	}

	@PostMapping("/new")
	@Operation(summary = "Создание нового интереса")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Успешно"),
		@ApiResponse(responseCode = "400", description = "Интерес уже существует"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<?> addInterest(@RequestBody InterestEntity interest) {
		interestService.createInterest(interest);
		return ResponseEntity.status(HttpStatus.CREATED).body("Интерес успешно создан");
	}

	@Transactional
	@DeleteMapping
	@Operation(summary = "Удаление интереса")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "404", description = "Интерес не найден"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<?> deleteInterest(@RequestParam String name){
		Optional<InterestEntity> result = interestService.deleteByName(name);
		if (result.isPresent())
			return ResponseEntity.ok("Интерес успешно удален");
		else
			return ResponseEntity.status(404).body("Интерес не найден");
	}
}