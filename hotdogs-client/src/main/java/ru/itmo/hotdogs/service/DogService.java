package ru.itmo.hotdogs.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
import ru.itmo.hotdogs.model.dto.ResponseDto;
import ru.itmo.hotdogs.model.dto.ShowDtoResponse;
import ru.itmo.hotdogs.model.dto.UserDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInteractionsEntity;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.repository.DogRepository;
import ru.itmo.hotdogs.rest.OwnerApi;
import ru.itmo.hotdogs.rest.UserApi;

@Service
@RequiredArgsConstructor
public class DogService {

	private final DogRepository dogRepository;
	private final DogsInteractionsService dogsInteractionsService;
	private final DogsInterestsService dogsInterestsService;

	private final InterestService interestService;
	private final BreedService breedService;

	private final Validator validator;
	private final OwnerApi ownerApi;
	private final UserApi userApi;

	public Page<DogEntity> findAll(Pageable pageable) {
		return dogRepository.findAll(pageable);
	}


	public DogEntity findByLogin(String login) throws NotFoundException {
		ResponseDto<UserEntity> response = userApi.findByLogin(login);
		if (!response.code().is2xxSuccessful())
			throw new NotFoundException(response.error().getMessage());
		return dogRepository.findByUser(response.body()).orElseThrow(
			() -> new NotFoundException("Собаки с таким логином не существует")
		);
	}

//	public Mono<DogEntity> findByLoginReactive(String login) throws NotFoundException {
//		return userApi.findByLogin(login)
//			.flatMap(user -> {
//				Optional<DogEntity> optionalDog = dogRepository.findByUser(user);
//				return optionalDog.<Mono<? extends DogEntity>>map(Mono::just).orElseGet(
//					() -> Mono.error(
//						new NotFoundException("Собаки с таким логином не существует")));
//			});
//	}

//	public Optional<DogEntity> findOptionalByLogin(String login) {
//		try {
//			UserEntity user = userApi.findByLogin(login);
//			return dogRepository.findByUser(user);
//
//		} catch (NotFoundException e) {
//			return Optional.empty();
//		}
//	}

	public DogEntity findById(long id) throws NotFoundException {
		return dogRepository.findById(id).orElseThrow(
			() -> new NotFoundException("Собаки с таким id не существует")
		);
	}

	public List<ShowDtoResponse> findAppliedShows(String login) throws NotFoundException {
		return findByLogin(login).getAppliedShows().stream().map(
				show -> new ShowDtoResponse(show.getDate(), show.getPrize(),
					show.getAllowedBreeds().stream().map(
						BreedEntity::toString).collect(Collectors.toSet()), show.getWinner().getName()))
			.toList();
	}

	public ResponseDto<?> applyToShow(DogEntity dog, Long showId)
		throws IllegalStateException, ServiceUnavalibleException {
		return ownerApi.addParticipant(showId, dog);
	}


	public DogEntity createNewDog(@Valid UserDto userDto, @Valid DogDto dogDto)
		throws AlreadyExistsException, NotFoundException, ConstraintViolationException, IllegalArgumentException, ServiceUnavalibleException, IllegalStateException {

		Set<ConstraintViolation<DogDto>> violations = validator.validate(dogDto);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		BreedEntity breed = breedService.findByName(dogDto.getBreed());
		OwnerEntity owner = ownerApi.findByLogin(dogDto.getOwnerLogin());

		for (Map.Entry<String, Integer> interest : dogDto.getInterests().entrySet()) {
			interestService.findByName(interest.getKey());
		}

		userDto.setRoles(Set.of("ROLE_DOG"));
		ResponseDto<UserEntity> response = userApi.createNewUser(userDto);
		if (!response.code().is2xxSuccessful()){
			throw new AlreadyExistsException(response.error().getMessage());
		}

		DogEntity dog = new DogEntity(response.body(), dogDto.getName(), dogDto.getAge(), breed, owner);

		dogRepository.save(dog);

		for (Map.Entry<String, Integer> interest : dogDto.getInterests().entrySet()) {
			DogsInterestsEntity interestsEntity = new DogsInterestsEntity(dog,
				interestService.findByName(interest.getKey()),
				interest.getValue());
			dogsInterestsService.save(interestsEntity);
		}

		return dogRepository.save(dog);
	}


	public RecommendedDog findNearest(DogEntity dog) throws NotFoundException {
		RecommendedDog recommendedDog = dogRepository.findNearest(
			dog.getOwner().getLocation().getX(),
			dog.getOwner().getLocation().getY(),
			dog.getId());
		if (recommendedDog == null) {
			throw new NotFoundException("Ты посмотрел всех, приходи позже");
		}
		dog.setCurRecommended(dogRepository.findById(recommendedDog.getId()).get());
		dogRepository.save(dog);
		return recommendedDog;

	}

	public Optional<RecommendedDog> rateRecommended(DogEntity dog, Boolean isLike)
		throws NullRecommendationException {

		DogEntity recommended = dog.getCurRecommended();
		RecommendedDog matched = null;

		if (recommended == null) {
			throw new NullRecommendationException();
		}
		if (dog.getInteractions() == null) {
			dog.setInteractions(new ArrayList<>());
		}

		DogsInteractionsEntity interaction = new DogsInteractionsEntity(dog, recommended, isLike);
		dogsInteractionsService.save(interaction);
		dog.getInteractions().add(interaction);
//		dogsInteractionsService.createInterest(interaction);
		dog.setCurRecommended(null);

		DogsInteractionsEntity reverseInteracted = dogsInteractionsService.findBySenderAndReceiver(
			recommended, dog);
		if (isLike && reverseInteracted != null && reverseInteracted.getIsLiked()) {
			if (dog.getMatches() == null) {
				dog.setMatches(new HashSet<>());
			}
			dog.getMatches().add(recommended);
			matched = dogRepository.findDistance(
				recommended.getId(),
				recommended.getOwner().getLocation().getX(),
				recommended.getOwner().getLocation().getY(),
				dog.getOwner().getLocation().getX(),
				dog.getOwner().getLocation().getY());
		}

		dogRepository.save(dog);
		return Optional.ofNullable(matched);
	}


	public DogEntity addInterest(DogEntity dog, @Valid DogInterestDto interestDto)
		throws AlreadyExistsException, NotFoundException, IllegalLevelException, ConstraintViolationException {

		Set<ConstraintViolation<DogInterestDto>> violations = validator.validate(interestDto);
		if (!validator.validate(interestDto).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		InterestEntity interest = interestService.findByName(interestDto.getInterestName());

		if (dog.getInterests() == null) {
			dog.setInterests(new ArrayList<>());
		}

		if (dog.getInterests().stream()
			.anyMatch((x) -> x.getInterest().getName().equals(interest.getName()))) {
			throw new AlreadyExistsException(
				"У данной собаки уже существует такой интерес.");
		}

		DogsInterestsEntity interest_record = new DogsInterestsEntity(dog, interest,
			interestDto.getLevel());

		dog.getInterests().add(interest_record);
		dogsInterestsService.save(interest_record);
		return dogRepository.save(dog);
	}


	public void deleteAll() {
		dogRepository.deleteAll();
	}

	public void deleteRecommendationsViaBreed(BreedEntity breed) {
		List<DogEntity> dogs = dogRepository.findByBreed(breed);
		for (DogEntity dog : dogs) {
			deleteFromRecommendations(dog);
		}
	}

	public void deleteRecommendationsViaOwner(OwnerEntity owner) {
		List<DogEntity> dogs = dogRepository.findByOwner(owner);
		for (DogEntity dog : dogs) {
			deleteFromRecommendations(dog);
		}
	}

	public void deleteFromRecommendations(DogEntity recommendeddog) {
		List<DogEntity> dogs = dogRepository.findByCurRecommended(recommendeddog);
		for (DogEntity dog : dogs) {
			dog.setCurRecommended(null);
			dogRepository.save(dog);
		}
	}
}
