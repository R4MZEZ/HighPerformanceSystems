package ru.itmo.ownerservice.controller;

import java.util.List;
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
public class OwnerController {

	private final OwnerService ownerService;
	private final JwtUtils jwtUtils;

	@Value("${page-size}")
	private Integer PAGE_SIZE;


	@Transactional
	@GetMapping
	public ResponseEntity<List<OwnerEntity>> findAll(@RequestParam(defaultValue = "0") int page) {
		PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Order.asc("id")));
		Page<OwnerEntity> entityPage = ownerService.findAll(pageRequest);

		return ResponseEntity.ok().body(entityPage.getContent());
	}

	@GetMapping("/find/{login}")
	public OwnerEntity findByLogin(@PathVariable String login) throws NotFoundException {
		return ownerService.findByLogin(login)
			.orElseThrow(() -> new NotFoundException("Хозяин с таким логином не найден"));
	}

	@PostMapping(path = "/new")
	public ResponseEntity<?> registerOwner(@RequestBody RegistrationOwnerDto registrationOwnerDto) {
		try {
			ownerService.createNewOwner(registrationOwnerDto.getUserInfo(),
				registrationOwnerDto.getOwnerInfo());
			return ResponseEntity.status(HttpStatus.CREATED).body("Владелец успешно создан.");
		} catch (AlreadyExistsException | ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping(path = "/shows/create")
	public ResponseEntity<?> createShow(ServerHttpRequest request,
		@RequestBody ShowDtoRequest newShowDto) {
		try {
			ownerService.createShow(jwtUtils.getUsernameFromRequest(request), newShowDto);
			return ResponseEntity.status(HttpStatus.CREATED).body("Выставка успешно создана");
		} catch (NotFoundException | NotEnoughMoneyException | ConstraintViolationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping(path = "/shows/{showId}/finish")
	public ResponseEntity<?> finishShow(ServerHttpRequest request, @PathVariable Long showId,
		@RequestParam Long winnerId) {
		try {
			DogEntity winner = ownerService.finishShow(jwtUtils.getUsernameFromRequest(request),
				showId, winnerId);
			return ResponseEntity.ok(
				"Выставка успешно завершена! Победитель: %s!".formatted(winner.getName()));
		} catch (NotFoundException | AccessDeniedException | ShowDateException |
				 AlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping(path = "/shows/{showId}/addParticipant")
	public ResponseDto<?> addParticipant(@PathVariable Long showId, @RequestBody DogEntity dog){
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