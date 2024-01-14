package ru.itmo.ownerservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.ownerservice.utils.JwtUtils;
import ru.itmo.ownerservice.exceptions.AccessDeniedException;
import ru.itmo.ownerservice.exceptions.AlreadyExistsException;
import ru.itmo.ownerservice.exceptions.BreedNotAllowedException;
import ru.itmo.ownerservice.exceptions.CheatingException;
import ru.itmo.ownerservice.exceptions.NotEnoughMoneyException;
import ru.itmo.ownerservice.exceptions.NotFoundException;
import ru.itmo.ownerservice.exceptions.ShowDateException;
import ru.itmo.ownerservice.model.dto.RegistrationOwnerDto;
import ru.itmo.ownerservice.model.dto.ResponseDto;
import ru.itmo.ownerservice.model.dto.ShowDtoRequest;
import ru.itmo.ownerservice.model.entity.DogEntity;
import ru.itmo.ownerservice.model.entity.OwnerEntity;
import ru.itmo.ownerservice.service.OwnerService;


@RequiredArgsConstructor
@RequestMapping(path = "/owners")
@RestController
@Tag(name = "Кабинет владельца", description = "для управления владельцем и выставками")
public class OwnerController {

	private final OwnerService ownerService;
	private final JwtUtils jwtUtils;

	@Value("${page-size}")
	private Integer PAGE_SIZE;


	@Transactional
	@GetMapping
	@Operation(summary = "Получить всех владельцев")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "403", description = "Нет прав")})
	public ResponseEntity<List<OwnerEntity>> findAll(
		@RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы для пагинации") int page) {
		PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Order.asc("id")));
		Page<OwnerEntity> entityPage = ownerService.findAll(pageRequest);

		return ResponseEntity.ok().body(entityPage.getContent());
	}

	@GetMapping("/find/{login}")
	@Operation(summary = "Получить владельца по логину",
		description = "Получение владельца по логину его аккаунта")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "404", description = "Хозяин с таким логином не найден"),
		@ApiResponse(responseCode = "403", description = "Нет прав")})
	public OwnerEntity findByLogin(@PathVariable String login)
		throws NotFoundException, ExecutionException, InterruptedException {
		return ownerService.findByLogin(login)
			.orElseThrow(() -> new NotFoundException("Хозяин с таким логином не найден"));
	}

	@PostMapping(path = "/new")
	@Operation(summary = "Регистрация нового владельца",
		description = "Регистрация нового владельца супервайзером")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "400", description = "Некорректно введены данные"),
		@ApiResponse(responseCode = "403", description = "Нет прав")})
	public ResponseEntity<?> registerOwner(@RequestBody @Parameter(description = "Данные для регистрации") RegistrationOwnerDto registrationOwnerDto) {
		try {
			ownerService.createNewOwner(registrationOwnerDto.getUserInfo(),
				registrationOwnerDto.getOwnerInfo());
			return ResponseEntity.status(HttpStatus.CREATED).body("Владелец успешно создан.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping(path = "/shows/create")
	@Operation(summary = "Создание новой выставки")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Успешно"),
		@ApiResponse(responseCode = "400", description = "Некорректно введены данные"),
		@ApiResponse(responseCode = "403", description = "Нет прав")})
	public ResponseEntity<?> createShow(ServerHttpRequest request,
		@RequestBody @Parameter(description = "Описание новой выставки") ShowDtoRequest newShowDto) {
		try {
			ownerService.createShow(jwtUtils.getUsernameFromRequest(request), newShowDto);
			return ResponseEntity.status(HttpStatus.CREATED).body("Выставка успешно создана");
		} catch (NotFoundException | NotEnoughMoneyException | ConstraintViolationException | ExecutionException | InterruptedException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping(path = "/shows/{showId}/finish")
	@Operation(summary = "Завершение выставки",
		description = "Завершить выставку с назначение победителя и начислением приза")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "400", description = "Ошибка пользователя"),
		@ApiResponse(responseCode = "403", description = "Нет прав")})
	public ResponseEntity<?> finishShow(ServerHttpRequest request, @PathVariable @Parameter(description = "Идентификатор выставки") Long showId,
		@RequestParam @Parameter(description = "Идентификатор победителя") Long winnerId) {
		try {
			DogEntity winner = ownerService.finishShow(jwtUtils.getUsernameFromRequest(request),
				showId, winnerId);
			return ResponseEntity.ok(
				"Выставка успешно завершена! Победитель: %s!".formatted(winner.getName()));
		} catch (NotFoundException | AccessDeniedException | ShowDateException |
				 AlreadyExistsException | InterruptedException | ExecutionException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping(path = "/shows/{showId}/addParticipant")
	@Operation(summary = "Добавление участника выставки",
		description = "Добавление нового участника в существующую выставку")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "400", description = "Нарушение каких-то правил выставки"),
		@ApiResponse(responseCode = "404", description = "Выставка не найдена"),
		@ApiResponse(responseCode = "403", description = "Порода не разрешена")})
	public ResponseDto<?> addParticipant(@PathVariable @Parameter(description = "Идентификатор выставки") Long showId, @RequestBody @Parameter(description = "Новый участник") DogEntity dog) {
		try {
			ownerService.addParticipant(showId, dog);
			return new ResponseDto<>(null, null, HttpStatus.OK);
		} catch (CheatingException | AlreadyExistsException | ShowDateException e) {
			return new ResponseDto<>(null, e, HttpStatus.BAD_REQUEST);
		} catch (NotFoundException e) {
			return new ResponseDto<>(null, e, HttpStatus.NOT_FOUND);
		} catch (BreedNotAllowedException e) {
			return new ResponseDto<>(null, e, HttpStatus.FORBIDDEN);
		}
	}

}