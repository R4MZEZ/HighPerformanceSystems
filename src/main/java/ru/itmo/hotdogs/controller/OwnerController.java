package ru.itmo.hotdogs.controller;

import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.model.dto.NewOwnerDto;
import ru.itmo.hotdogs.model.dto.NewShowDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.service.OwnerService;
import ru.itmo.hotdogs.utils.ControllerConfig;


@RequiredArgsConstructor
@RequestMapping(path = "/owners")
@RestController
public class OwnerController {

	private final OwnerService ownerService;

	@GetMapping
	public ResponseEntity<List<OwnerEntity>> findAll(@RequestParam(defaultValue = "0") int page) {
		PageRequest pageRequest = PageRequest.of(page, ControllerConfig.pageSize, Sort.by(Sort.Order.asc("id")));
		Page<OwnerEntity> entityPage = ownerService.findAll(pageRequest);

		return ResponseEntity.ok().body(entityPage.getContent());
	}

	@PostMapping(path = "/new")
	public ResponseEntity<?> createOwner(@RequestBody NewOwnerDto owner) {
		try{
			return ResponseEntity.ok(ownerService.createNewOwner(owner));
		} catch (AlreadyExistsException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping(path = "/shows/create")
	public ResponseEntity<?> createShow(Principal principal, @RequestBody NewShowDto newShowDto) {
		try {
			ownerService.createShow(principal.getName(), newShowDto);
			return ResponseEntity.ok("Выставка успешно создана");
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