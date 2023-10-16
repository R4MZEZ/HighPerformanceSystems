package ru.itmo.hotdogs.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.exceptions.AccessDeniedException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.dto.ShowDto;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.service.OwnerService;


@RequiredArgsConstructor
@RequestMapping(path = "/owners")
@RestController
public class OwnerController {

	private final OwnerService ownerService;

	@GetMapping
	public ResponseEntity<List<OwnerEntity>> findAll() {
		return ResponseEntity.ok(ownerService.findAll());
	}

	@PostMapping(path = "/new")
	public ResponseEntity<?> createOwner(@RequestBody OwnerEntity owner) {
		ownerService.save(owner);
		return ResponseEntity.ok("Владелец успешно создан");
	}

	@PostMapping(path = "/{id}/create-show")
	public ResponseEntity<?> createShow(@PathVariable long id, @RequestBody ShowDto showDto) {
		try {
			ownerService.createShow(id, showDto);
			return ResponseEntity.ok("Выставка успешно создана");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
		}
	}
}