package ru.itmo.hotdogs.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.BreedNotAllowedException;
import ru.itmo.hotdogs.exceptions.CheatingException;
import ru.itmo.hotdogs.exceptions.IllegalLevelException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.NullRecommendationException;
import ru.itmo.hotdogs.exceptions.ShowDateException;
import ru.itmo.hotdogs.model.dto.ExistingShowDto;
import ru.itmo.hotdogs.model.dto.NewDogDto;
import ru.itmo.hotdogs.model.dto.RecommendedDogDto;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;
import ru.itmo.hotdogs.service.DogService;
import ru.itmo.hotdogs.utils.ControllerConfig;

@RequiredArgsConstructor
@RequestMapping(path = "/dogs")
@RestController
public class DogController {

	private DogService dogService;

	@Autowired
	public void setDogService(DogService dogService) {
		this.dogService = dogService;
	}

	@PostMapping(path = "/new")
	public ResponseEntity<?> createNewDog(@RequestBody NewDogDto dog) {
		try{
			return ResponseEntity.status(HttpStatus.CREATED).body(dogService.createNewDog(dog));
		} catch (AlreadyExistsException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping(path = "/rate")
	public ResponseEntity<?> likeRecommended(Principal principal, @RequestParam(defaultValue = "true") boolean is_like) {
		try {
			RecommendedDogDto matchedDog = dogService.rateRecommended(principal.getName(), is_like);
			if (matchedDog != null) {
				return new ResponseEntity<>(
					"It's a match! With \n%s, %d\n%.1f km away.".formatted(
						matchedDog.getName(),
						matchedDog.getAge(),
						matchedDog.getDistance() / 1000), HttpStatus.FOUND);
			} else {
				return ResponseEntity.ok(getNewRecommendation(principal));
			}
		} catch (NullRecommendationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/recommend")
	public ResponseEntity<?> getNewRecommendation(Principal principal) {
		try {
			return ResponseEntity.ok(dogService.findNearest(principal.getName()));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PatchMapping("/add-interest")
	public ResponseEntity<?> addInterest(Principal principal, @RequestParam int id,
		@RequestParam int level) {
		try {
			dogService.addInterest(principal.getName(), id, level);
			return ResponseEntity.ok("Интерес успешно добавлен собаке.");
		} catch (AlreadyExistsException | IllegalLevelException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/shows")
	public ResponseEntity<?> appliedShows(Principal principal, @RequestParam(defaultValue = "0") int page){
		try {
			List<ExistingShowDto> result = dogService.findAppliedShows(principal.getName());
			int fromIndex = result.size() > page * ControllerConfig.pageSize ? page * ControllerConfig.pageSize : 0;
			int toIndex = Math.min(result.size(), (page + 1) * ControllerConfig.pageSize);
			return ResponseEntity.ok(result.subList(fromIndex, toIndex));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/shows/{showId}/apply")
	public ResponseEntity<?> applyToShow(Principal principal, @PathVariable Long showId){
		try {
			dogService.applyToShow(principal.getName(), showId);
			return ResponseEntity.ok("Вы стали участником выставки!");
		} catch (CheatingException | AlreadyExistsException | ShowDateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (BreedNotAllowedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<List<NewDogDto>> findAll(@RequestParam(defaultValue = "0") int page) {
		PageRequest pageRequest = PageRequest.of(page, ControllerConfig.pageSize, Sort.by(Sort.Order.asc("id")));
		Page<DogEntity> entityPage = dogService.findAll(pageRequest);

		return ResponseEntity.ok()
			.header("X-Total-Count", String.valueOf(entityPage.getTotalElements()))
			.body(entityPage.getContent().stream().map(dog -> new NewDogDto(
				dog.getName(),
				dog.getAge(),
				dog.getBreed().getName(),
				dog.getOwner().getUser().getLogin(),
				dog.getInterests().stream().collect(Collectors.toMap(
						interest -> interest.getInterest().getName(),
						DogsInterestsEntity::getLevel)))).toList());
	}

	@GetMapping("/me")
	public ResponseEntity<?> getInfo(Principal principal) {
		try {
			return ResponseEntity.ok(dogService.findByLogin(principal.getName()));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

}
