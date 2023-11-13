package ru.itmo.hotdogs.controller;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	@Transactional
	@DeleteMapping
	public ResponseEntity<?> deleteUser(@RequestParam String login) {
		Optional<UserEntity> result = userService.deleteByLogin(login);
		if (result.isPresent())
			return ResponseEntity.ok("Пользователь успешно удален");
		else
			return ResponseEntity.badRequest().body("Пользователь не найден");

	}


}
