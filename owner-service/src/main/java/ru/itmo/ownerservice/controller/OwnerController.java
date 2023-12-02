package ru.itmo.ownerservice.controller;

import java.security.Principal;
import java.util.List;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.ownerservice.exceptions.AlreadyExistsException;
import ru.itmo.ownerservice.model.dto.RegistrationOwnerDto;
import ru.itmo.ownerservice.model.dto.ShowDtoRequest;
import ru.itmo.ownerservice.model.entity.DogEntity;
import ru.itmo.ownerservice.model.entity.OwnerEntity;
import ru.itmo.ownerservice.service.OwnerService;


@RequiredArgsConstructor
@RequestMapping(path = "/owners")
@RestController
public class OwnerController {

	private final OwnerService ownerService;

	@Value("${page-size}")
	private int PAGE_SIZE;


	@GetMapping
	public ResponseEntity<List<OwnerEntity>> findAll(@RequestParam(defaultValue = "0") int page) {
		PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Order.asc("id")));
		Page<OwnerEntity> entityPage = ownerService.findAll(pageRequest);

		return ResponseEntity.ok().body(entityPage.getContent());
	}

	@PostMapping(path = "/new")
	public ResponseEntity<?> registerOwner(@RequestBody RegistrationOwnerDto registrationOwnerDto) {
		try{
			ownerService.createNewOwner(registrationOwnerDto.getUserInfo(), registrationOwnerDto.getOwnerInfo());
			return ResponseEntity.status(HttpStatus.CREATED).body("Владелец успешно создан.");
		} catch (AlreadyExistsException | ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping(path = "/shows/create")
	public ResponseEntity<?> createShow(Principal principal, @RequestBody ShowDtoRequest newShowDto) {
		try {
			ownerService.createShow(principal.getName(), newShowDto);
			return ResponseEntity.status(HttpStatus.CREATED).body("Выставка успешно создана");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
		}
	}

	@PostMapping(path = "/shows/{showId}/finish")
	public ResponseEntity<?> finishShow(Principal principal, @PathVariable Long showId, @RequestParam Long winnerId){
		try {
			DogEntity winner = ownerService.finishShow(principal.getName(), showId, winnerId);
			return ResponseEntity.ok("Выставка успешно завершена! Победитель: %s!".formatted(winner.getName()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
		}
	}
}