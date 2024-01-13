package ru.itmo.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.itmo.userservice.exceptions.AlreadyExistsException;
import ru.itmo.userservice.exceptions.NotFoundException;
import ru.itmo.userservice.model.dto.ResponseDto;
import ru.itmo.userservice.model.dto.UserDto;
import ru.itmo.userservice.model.entity.UserEntity;
import ru.itmo.userservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Аккаунты", description = "управляет регистрационной информацией")
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


	@Operation(description = "Создание аккаунта",
		summary = "Создать новую пару логин/пароль")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "400", description = "Пользователь с таким логином уже существует, или некорректно введена схема данных"),
		@ApiResponse(responseCode = "404", description = "Не найдена какая-то сущность"),
		@ApiResponse(responseCode = "403", description = "Нет прав")})

	@PostMapping("/new")
	public Mono<ResponseDto<UserEntity>> createNewUser(
		@RequestBody @Parameter(description = "Данные регистрации") UserDto userDto) {
		return userService.createNewUser(userDto).onErrorReturn(new UserEntity(-1L))
			.map(userEntity -> {
					if (userEntity.getId() == null) {
						return new ResponseDto<>(null,
							new AlreadyExistsException(
								"Пользователь с таким логином уже существует"),
							HttpStatus.BAD_REQUEST);
					} else if (userEntity.getId() == -1L) {
						return new ResponseDto<>(null,
							new IllegalArgumentException(
								"Проверьте правильность введенных данных о userInfo"),
							HttpStatus.BAD_REQUEST);
					} else {
						return new ResponseDto<>(userEntity, null, HttpStatus.OK);
					}
				}
			);
	}

	@Operation(description = "Поиск по логину",
		summary = "Получить пользователя по логину")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "404", description = "Не найдена какая-то сущность"),
		@ApiResponse(responseCode = "403", description = "Нет прав")})

	@GetMapping("/find")
	public ResponseDto<UserEntity> findByLogin(@RequestParam String login) {
		UserEntity user = userService.findByLogin(login).block();
		if (user == null || user.getId() == null) {
			return new ResponseDto<>(null, new NotFoundException(""), HttpStatus.NOT_FOUND);
		} else {
			return new ResponseDto<>(user, null, HttpStatus.OK);
		}
	}

	@Operation(description = "Добавление роли пользователю",
		summary = "Добавить существующему пользователю новую роль")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "404", description = "Пользователь не найден"),
		@ApiResponse(responseCode = "403", description = "Нет прав")})
	@PostMapping("/addRole")
	public Mono<ResponseDto<UserEntity>> addRole(
		@RequestParam @Parameter(description = "Идентификатор пользователя") Long userId,
		@RequestParam @Parameter(description = "Идентификатор роли") Integer roleId) {
		return userService.addRole(userId, roleId).map(userEntity ->
			new ResponseDto<>(userEntity, null, HttpStatus.OK)
		);
	}
}



