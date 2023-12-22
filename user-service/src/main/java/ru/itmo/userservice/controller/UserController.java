package ru.itmo.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.userservice.exceptions.AlreadyExistsException;
import ru.itmo.userservice.exceptions.NotFoundException;
import ru.itmo.userservice.model.dto.ResponseDto;
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
	public ResponseDto<UserEntity> createNewUser(@RequestBody UserDto userDto) {
		UserEntity user = userService.createNewUser(userDto).block();
		if (user == null || user.getId() == null)
			return new ResponseDto<>(null, new AlreadyExistsException("Пользователь с таким логином уже существует"), HttpStatus.BAD_REQUEST);
		else return new ResponseDto<>(user, null, HttpStatus.OK);
	}

	@GetMapping("/find")
	public ResponseDto<UserEntity> findByLogin(@RequestParam String login) {
		UserEntity user = userService.findByLogin(login).block();
		if (user== null || user.getId() == null)
			return new ResponseDto<>(null, new NotFoundException(""), HttpStatus.NOT_FOUND);
		else return new ResponseDto<>(user, null, HttpStatus.OK);
	}


}
