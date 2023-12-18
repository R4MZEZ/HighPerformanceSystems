package ru.itmo.hotdogs.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
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
import reactor.core.publisher.Mono;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.BreedNotAllowedException;
import ru.itmo.hotdogs.exceptions.CheatingException;
import ru.itmo.hotdogs.exceptions.IllegalLevelException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.NullRecommendationException;
import ru.itmo.hotdogs.exceptions.ServiceUnavalibleException;
import ru.itmo.hotdogs.exceptions.ShowDateException;
import ru.itmo.hotdogs.model.dto.DogDto;
import ru.itmo.hotdogs.model.dto.DogInterestDto;
import ru.itmo.hotdogs.model.dto.RecommendedDog;
import ru.itmo.hotdogs.model.dto.RecommendedDogDto;
import ru.itmo.hotdogs.model.dto.RegistrationDogDto;
import ru.itmo.hotdogs.model.dto.ResponseDto;
import ru.itmo.hotdogs.model.dto.ShowDtoResponse;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.service.DogService;
import ru.itmo.hotdogs.utils.ControllerConfig;
import ru.itmo.hotdogs.utils.DtoConverter;
import ru.itmo.hotdogs.utils.JwtUtils;

@RequiredArgsConstructor
@RequestMapping("/dogs")
@RestController
public class DogController {

	private final DogService dogService;
	private final JwtUtils jwtUtils;


	@GetMapping("/test")
	public String test(){
		return "test";
	}

	@PostMapping(path = "/new")
	public ResponseEntity<?> registerNewDog(@RequestBody RegistrationDogDto registrationDogDto) {
		try {
			dogService.createNewDog(registrationDogDto.getUserInfo(), registrationDogDto.getDogInfo());
			return ResponseEntity.status(HttpStatus.CREATED).body("Собака успешно создана");
		} catch (AlreadyExistsException | ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e){
			return ResponseEntity.badRequest().body("Некорректный формат описания объекта");
		} catch (ServiceUnavalibleException | IllegalStateException e){
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
		}
	}

	@PostMapping(path = "/rate")
	public ResponseEntity<?> likeRecommended(ServerHttpRequest request,
		@RequestParam(defaultValue = "true") boolean isLike) {
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
				return ResponseEntity.ok(getNewRecommendation(request));
			}
		} catch (NullRecommendationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/recommend")
	public ResponseEntity<?> getNewRecommendation(ServerHttpRequest request) {
		try {
			DogEntity dog = dogService.findByLogin(jwtUtils.getUsernameFromRequest(request));
			return ResponseEntity.ok(dogService.findNearest(dog));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PatchMapping("/add-interest")
	public ResponseEntity<?> addInterest(ServerHttpRequest request,
		@RequestBody DogInterestDto interestDto) {
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
	public ResponseEntity<?> appliedShows(ServerHttpRequest request,
		@RequestParam(defaultValue = "0") int page) {
		try {
			List<ShowDtoResponse> result = dogService.findAppliedShows(
				jwtUtils.getUsernameFromRequest(request));
			int fromIndex =
				result.size() > page * ControllerConfig.PAGE_SIZE
					? page * ControllerConfig.PAGE_SIZE
					: 0;
			int toIndex = Math.min(result.size(), (page + 1) * ControllerConfig.PAGE_SIZE);
			return ResponseEntity.ok(result.subList(fromIndex, toIndex));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/shows/{showId}/apply")
	public ResponseEntity<?> applyToShow(ServerHttpRequest request, @PathVariable Long showId) {
		try {
			DogEntity dog = dogService.findByLogin(jwtUtils.getUsernameFromRequest(request));
			ResponseDto<?> response = dogService.applyToShow(dog, showId);
			if (response.code() != HttpStatus.OK)
				return ResponseEntity.status(response.code()).body(response.error().getMessage());
			return ResponseEntity.ok("Вы стали участником выставки!");
		} catch (ServiceUnavalibleException | IllegalStateException e){
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<List<DogDto>> findAll(@RequestParam(defaultValue = "0") int page) {
		PageRequest pageRequest = PageRequest.of(page, ControllerConfig.PAGE_SIZE,
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
	public ResponseEntity<?> getInfo(ServerHttpRequest request) {
		try {
			DogEntity dog = dogService.findByLogin(jwtUtils.getUsernameFromRequest(request));
			return ResponseEntity.ok(DtoConverter.dogEntityToDto(dog));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
//	@Transactional
//	@GetMapping("/me")
//	public Mono<ResponseEntity<DogDto>> getInfo(ServerHttpRequest request) throws NotFoundException {
//		return dogService.findByLoginReactive(jwtUtils.getUsernameFromRequest(request))
//			.map(dogEntity -> ResponseEntity.ok().body(DtoConverter.dogEntityToDto(dogEntity)))
//			.defaultIfEmpty(ResponseEntity.notFound().build());
//	}

	@GetMapping("/find/{id}")
	public ResponseDto<DogEntity> findById(@PathVariable Long id) {
		try {
			return new ResponseDto<>(dogService.findById(id), null, HttpStatus.OK);
		} catch (NotFoundException e){
			return new ResponseDto<>(null, e, HttpStatus.NOT_FOUND);
		}
	}

}
