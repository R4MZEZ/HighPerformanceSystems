package ru.itmo.hotdogs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.IllegalLevelException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.NullRecommendationException;
import ru.itmo.hotdogs.exceptions.ServiceUnavalibleException;
import ru.itmo.hotdogs.model.dto.DogDto;
import ru.itmo.hotdogs.model.dto.DogInterestDto;
import ru.itmo.hotdogs.model.dto.RecommendedDog;
import ru.itmo.hotdogs.model.dto.RegistrationDogDto;
import ru.itmo.hotdogs.model.dto.ResponseDto;
import ru.itmo.hotdogs.model.dto.ShowDtoResponse;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;
import ru.itmo.hotdogs.service.DogService;
import ru.itmo.hotdogs.utils.DtoConverter;
import ru.itmo.hotdogs.utils.JwtUtils;

@RequiredArgsConstructor
@RequestMapping("/dogs")
@RestController
@Tag(name = "Собачья будка", description = "Управляет делами собак и их взаимоотношениями")
public class DogController {

	private final DogService dogService;
	private final JwtUtils jwtUtils;

	@Value("${page-size}")
	Integer pageSize;

	@PostMapping(path = "/new")
	@Operation(summary = "Создание новой собаки")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Успешно"),
		@ApiResponse(responseCode = "400", description = "Ошибка в введенных данных"),
		@ApiResponse(responseCode = "404", description = "Владелец не найден"),
		@ApiResponse(responseCode = "503", description = "Какой-то сервис недоступен"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<?> registerNewDog(
		@RequestBody @Parameter(description = "Данные новой собаки") RegistrationDogDto registrationDogDto) {
		try {
			dogService.createNewDog(registrationDogDto.getUserInfo(),
				registrationDogDto.getDogInfo());
			return ResponseEntity.status(HttpStatus.CREATED).body("Собака успешно создана");
		} catch (AlreadyExistsException | ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Некорректный формат описания объекта");
		} catch (ServiceUnavalibleException | IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
		}
	}

	@PostMapping(path = "/rate")
	@Operation(summary = "Оценка рекомендованной собаки (Свайп)", description = "Когда в профиле есть рекомендованная собака, ее можно лайкнуть/дизлайкнуть")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "302", description = "Достигнуто совпадение"),
		@ApiResponse(responseCode = "400", description = "Невозможно поставить оценку"),
		@ApiResponse(responseCode = "404", description = "Какой-то ресурс не найден"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<?> likeRecommended(ServerHttpRequest request,
		@RequestParam(defaultValue = "true") @Parameter(description = "Оценка - лайк или дизлайк") boolean isLike) {
		try {
			DogEntity dog = dogService.findByLogin(jwtUtils.getUsernameFromRequest(request));
			Optional<RecommendedDog> matchedDog = dogService.rateRecommended(dog, isLike);
			if (matchedDog.isPresent()) {
				return new ResponseEntity<>(
					"It's a match! With \n%s, %d\n%.1f km away.".formatted(
						matchedDog.get().getName(),
						matchedDog.get().getAge(),
						matchedDog.get().getDistance() / 1000), HttpStatus.FOUND);
			} else {
				return getNewRecommendation(request);
			}
		} catch (NullRecommendationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/recommend")
	@Operation(summary = "Получение рекомендации", description = "Выдает существующую рекомендацию (при наличии) или выдает новую")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "404", description = "Какой-то ресурс не найден"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<?> getNewRecommendation(ServerHttpRequest request) {
		try {
			DogEntity dog = dogService.findByLogin(jwtUtils.getUsernameFromRequest(request));
			return ResponseEntity.ok(dogService.findNearest(dog));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PatchMapping("/interests/add")
	@Operation(summary = "Добавление интереса собаке", description = "Выдает существующей собаке существующий интерес")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "400", description = "Ошибка в введенных данных"),
		@ApiResponse(responseCode = "404", description = "Какой-то ресурс не найден"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<?> addInterest(ServerHttpRequest request,
		@RequestBody @Parameter(description = "Информация для добавления интереса") DogInterestDto interestDto) {
		try {
			DogEntity dog = dogService.findByLogin(jwtUtils.getUsernameFromRequest(request));
			dogService.addInterest(dog, interestDto);
			return ResponseEntity.ok("Интерес успешно добавлен собаке.");
		} catch (AlreadyExistsException | IllegalLevelException | ConstraintViolationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/shows")
	@Operation(summary = "Получение выставок, на которые подана заявка", description = "Выдает список всех выставок (завершенных и актуальных) в участниках которой числится данная собака")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "404", description = "Какой-то ресурс не найден"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<?> appliedShows(ServerHttpRequest request,
		@RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы для пагинации") int page) {
		try {
			List<ShowDtoResponse> result = dogService.findAppliedShows(
				jwtUtils.getUsernameFromRequest(request));
			int fromIndex =
				result.size() > page * pageSize
					? page * pageSize
					: 0;
			int toIndex = Math.min(result.size(), (page + 1) * pageSize);
			return ResponseEntity.ok(result.subList(fromIndex, toIndex));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/shows/{showId}/apply")
	@Operation(summary = "Подача заявки на выставку")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "503", description = "Какой-то сервис не доступен"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<?> applyToShow(ServerHttpRequest request, @PathVariable Long showId) {
		try {
			DogEntity dog = dogService.findByLogin(jwtUtils.getUsernameFromRequest(request));
			ResponseDto<?> response = dogService.applyToShow(dog, showId);
			if (response.code() != HttpStatus.OK) {
				return ResponseEntity.status(response.code()).body(response.error().getMessage());
			}
			return ResponseEntity.ok("Вы стали участником выставки!");
		} catch (ServiceUnavalibleException | IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

	@GetMapping
	@Operation(summary = "Получение списка собак", description = "Выдает список всех существующих собак")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<List<DogDto>> findAll(@RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы для пагинации") int page) {
		PageRequest pageRequest = PageRequest.of(page, pageSize,
			Sort.by(Sort.Order.asc("id")));
		Page<DogEntity> entityPage = dogService.findAll(pageRequest);

		return ResponseEntity.ok()
			.header("X-Total-Count", String.valueOf(entityPage.getTotalElements()))
			.body(entityPage.getContent().stream().map(dog -> new DogDto(
				dog.getName(),
				dog.getAge(),
				dog.getBreed().getName(),
				dog.getOwner().getUser().getLogin(),
				dog.getInterests().stream().collect(Collectors.toMap(
					interest -> interest.getInterest().getName(),
					DogsInterestsEntity::getLevel)))).toList());
	}

	@Transactional
	@GetMapping("/me")
	@Operation(summary = "Получение информации о текущей собаке", description = "Выдает информацию об аутентифицированной в данный момент собаке")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "404", description = "Какой-то ресурс не найден"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseEntity<?> getInfo(ServerHttpRequest request) {
		try {
			DogEntity dog = dogService.findByLogin(jwtUtils.getUsernameFromRequest(request));
			return ResponseEntity.ok(DtoConverter.dogEntityToDto(dog));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/find/{id}")
	@Operation(summary = "Получение собаки по идентификатору")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Успешно"),
		@ApiResponse(responseCode = "404", description = "Собака не найдена"),
		@ApiResponse(responseCode = "403", description = "Недостаточно прав")})
	public ResponseDto<DogEntity> findById(@PathVariable Long id) {
		try {
			return new ResponseDto<>(dogService.findById(id), null, HttpStatus.OK);
		} catch (NotFoundException e) {
			return new ResponseDto<>(null, e, HttpStatus.NOT_FOUND);
		}
	}

}
