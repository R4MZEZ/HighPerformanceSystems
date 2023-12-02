package ru.itmo.userservice.controller;

import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.userservice.exceptions.AlreadyExistsException;
import ru.itmo.userservice.exceptions.NotFoundException;
import ru.itmo.userservice.model.dto.UserDto;
import ru.itmo.userservice.model.entity.UserEntity;
import ru.itmo.userservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

//	@Transactional
//	@DeleteMapping
//	public ResponseEntity<?> deleteUser(@RequestParam String login) {
//		Optional<UserEntity> result = userService.deleteByLogin(login);
//		if (result.isPresent())
//			return ResponseEntity.ok("Пользователь успешно удален");
//		else
//			return ResponseEntity.badRequest().body("Пользователь не найден");
//
//	}

	@PostMapping("/new")
	public UserEntity createNewUser(@RequestBody UserDto userDto) throws AlreadyExistsException {

		return userService.createNewUser(userDto);
	}

	@GetMapping("/find")
	public UserEntity findByLogin(@RequestParam String login) throws NotFoundException {

		return userService.findByLogin(login);
	}


}
