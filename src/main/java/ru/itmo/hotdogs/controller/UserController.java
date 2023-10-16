package ru.itmo.hotdogs.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.dto.RecommendedUserDto;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.service.UserService;

@RequiredArgsConstructor
@RequestMapping(path = "/users")
@RestController
public class UserController {

	private final UserService userService;

	@PostMapping(path = "/new")
	public ResponseEntity<?> createUser(@RequestBody RecommendedUserDto user) {
		userService.save(user);
		return ResponseEntity.ok("Пользователь успешно создан");
	}

	@PostMapping(path = "/{id}/rate")
	public ResponseEntity<?> likeRecommended(@PathVariable long id,
		@RequestParam boolean is_like) {
		try {
			RecommendedUserDto matchedUser = userService.rateRecommended(id, is_like);
			if (matchedUser != null) {
				return new ResponseEntity<>(
					"It's a match! With \n%s, %d\n%.1f km away.".formatted(
						matchedUser.getName(),
						matchedUser.getAge(),
						matchedUser.getDistance() / 1000), HttpStatus.FOUND);
			} else {
				return ResponseEntity.ok(getNewRecommendation(id));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
		}
	}

	@GetMapping("/{id}/recommend")
	public ResponseEntity<?> getNewRecommendation(@PathVariable long id) {
		try {
			return ResponseEntity.ok(userService.findNearest(id));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
		}
	}

	@PatchMapping("/{userId}/add-interest")
	public ResponseEntity<?> addInterest(@PathVariable long userId, @RequestParam int id,
		@RequestParam int level) {
		try {
			userService.addInterest(userId, id, level);
			return ResponseEntity.ok("Интерес успешно добавлен пользователю.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
		}
	}

	@GetMapping()
	public ResponseEntity<List<UserEntity>> findAll() {
		return ResponseEntity.ok(userService.findAll());
	}

	@GetMapping("{id}")
	public ResponseEntity<?> findById(@PathVariable long id) {
		try {
			return ResponseEntity.ok(userService.findById(id));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
		}
	}

}
