package ru.itmo.hotdogs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.dto.ResponseDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.service.BreedService;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/dogs/breeds")
@Tag(name = "Реестр пород")
public class BreedsController {

	private final BreedService breedService;

	@Value("${page-size}")
	Integer pageSize;

	@Transactional
	@DeleteMapping
	@Operation(summary = "Удаление существующей породы")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "404", description = "Порода не найдена"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<?> deleteBreed(@RequestParam String name) {
		Optional<BreedEntity> result = breedService.deleteByName(name);
		if (result.isPresent()) {
			return ResponseEntity.ok("Порода успешно удалена");
		} else {
			return ResponseEntity.status(404).body("Порода не найдена");
		}
	}

	@GetMapping
	@Operation(summary = "Получение всех пород", description = "Вывести список всех пород")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<List<BreedEntity>> findAll(@RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы для пагинации") int page) {
		PageRequest pageRequest = PageRequest.of(page, pageSize,
			Sort.by(Sort.Order.asc("id")));
		Page<BreedEntity> entityPage = breedService.findAll(pageRequest);

		return ResponseEntity.ok()
			.header("X-Total-Count", String.valueOf(entityPage.getTotalElements()))
			.body(entityPage.getContent());
	}

	@PostMapping("/new")
	@Operation(summary = "Создание новой породы")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Успешно"),
		@ApiResponse(responseCode = "400", description = "Порода уже существует"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<?> addBreed(@RequestBody @Parameter(description = "Описание новой породы") BreedEntity breed) {
		try {
			breedService.createBreed(breed);
			return ResponseEntity.status(HttpStatus.CREATED).body("Порода успешно создана");
		} catch (AlreadyExistsException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@GetMapping("/find/{name}")
	@Operation(summary = "Получение породы по названию")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "404", description = "Порода не найдена"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseDto<BreedEntity> findBreedByName(@PathVariable String name) {
		try {
			return new ResponseDto<>(breedService.findByName(name), null, HttpStatus.OK);
		} catch (NotFoundException e){
			return new ResponseDto<>(null, e, HttpStatus.NOT_FOUND);
		}
	}
}