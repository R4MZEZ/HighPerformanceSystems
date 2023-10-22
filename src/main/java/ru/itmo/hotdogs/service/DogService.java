package ru.itmo.hotdogs.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.BreedNotAllowedException;
import ru.itmo.hotdogs.exceptions.CheatingException;
import ru.itmo.hotdogs.exceptions.IllegalLevelException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.NullRecommendationException;
import ru.itmo.hotdogs.exceptions.ShowDateException;
import ru.itmo.hotdogs.model.dto.ExistingShowDto;
import ru.itmo.hotdogs.model.dto.NewDogDto;
import ru.itmo.hotdogs.model.dto.NewDogInterestDto;
import ru.itmo.hotdogs.model.dto.RecommendedDogDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInteractionsEntity;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.repository.DogRepository;
import ru.itmo.hotdogs.repository.DogsInteractionsRepository;
import ru.itmo.hotdogs.repository.DogsInterestsRepository;

@Service
@RequiredArgsConstructor
public class DogService {

	private final DogRepository dogRepository;
	private final DogsInteractionsRepository dogsInteractionsRepository;
	private final InterestService interestService;
	private final DogsInterestsRepository dogsInterestsRepository;
	private final UserService userService;
	private final ShowService showService;
	private final BreedService breedService;
	private final Validator validator;
	private OwnerService ownerService;

	@Autowired
	public void setOwnerService(OwnerService ownerService) {
		this.ownerService = ownerService;
	}

	public Page<DogEntity> findAll(Pageable pageable) {
		return dogRepository.findAll(pageable);
	}


	public DogEntity findByLogin(String login) throws NotFoundException {
		UserEntity user = userService.findByLogin(login);
		return dogRepository.findByUser(user).orElseThrow(
			() -> new NotFoundException("Собаки с таким логином не существует")
		);
	}

	public DogEntity findById(long id) throws NotFoundException {
		return dogRepository.findById(id).orElseThrow(
			() -> new NotFoundException("Собаки с таким id не существует")
		);
	}

	public List<ExistingShowDto> findAppliedShows(String login) throws NotFoundException {
		return findByLogin(login).getAppliedShows().stream().map(
				show -> new ExistingShowDto(show.getDate(), show.getPrize(),
					show.getAllowedBreeds().stream().map(
						BreedEntity::toString).collect(Collectors.toSet()), show.getWinner().getName()))
			.toList();
	}

	public void applyToShow(String login, Long showId)
		throws NotFoundException, BreedNotAllowedException, ShowDateException, AlreadyExistsException, CheatingException {
		ShowEntity show = showService.findById(showId);
		DogEntity dog = findByLogin(login);

		if (!show.getAllowedBreeds().contains(dog.getBreed())) {
			throw new BreedNotAllowedException();
		}

		if (new Date().after(show.getDate())) {
			throw new ShowDateException("Выставка уже прошла");
		}

		if (show.getParticipants().contains(dog)) {
			throw new AlreadyExistsException("Вы уже участвуете в этой выставке");
		}

		if (show.getOrganizer().getId().equals(dog.getOwner().getId())) {
			throw new CheatingException();
		}

		showService.addParticipant(show, dog);
	}

	public NewDogDto createNewDog(@Valid NewDogDto newDogDto)
		throws AlreadyExistsException, NotFoundException, ConstraintViolationException {

		Set<ConstraintViolation<NewDogDto>> violations = validator.validate(newDogDto);
		if (!validator.validate(newDogDto).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		DogEntity dog;
		try {
			userService.findByLogin(newDogDto.getLogin());
			throw new AlreadyExistsException("Пользователь с указанным именем уже существует");
		} catch (NotFoundException ex) {
			UserEntity user = userService.createNewUser(newDogDto, List.of("ROLE_DOG"));
			dog = new DogEntity(user,
				newDogDto.getName(),
				newDogDto.getAge(),
				breedService.findByName(newDogDto.getBreed()),
				ownerService.findByLogin(newDogDto.getOwnerLogin()));

			List<DogsInterestsEntity> interests = new ArrayList<>();
			for (Map.Entry<String, Integer> interest : newDogDto.getInterests().entrySet()) {
				interests.add(
					new DogsInterestsEntity(dog, interestService.findByName(interest.getKey()),
						interest.getValue()));
			}
			dog.setInterests(interests);
		}
		dogRepository.save(dog);
		return new NewDogDto(
			newDogDto.getName(),
			newDogDto.getAge(),
			newDogDto.getBreed(),
			newDogDto.getOwnerLogin(),
			newDogDto.getInterests());
	}




	public RecommendedDogDto findNearest(String login) throws NotFoundException {
		DogEntity dog = findByLogin(login);
		RecommendedDogDto recommendedDog = dogRepository.findNearest(
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

	/**
	 * @return возвращает экземпляр RecommendedDogDto, если был достигнут мэтч, иначе - null
	 */
	public RecommendedDogDto rateRecommended(String login, boolean is_like)
		throws NotFoundException, NullRecommendationException {
		DogEntity dog = findByLogin(login);

		DogEntity recommended = dog.getCurRecommended();
		if (recommended == null) {
			throw new NullRecommendationException();
		}
		DogsInteractionsEntity interaction_record = new DogsInteractionsEntity(dog, recommended,
			is_like);
		dogsInteractionsRepository.save(interaction_record);

		dog.setCurRecommended(null);
		dogRepository.save(dog);

		DogsInteractionsEntity reverse_interacted = dogsInteractionsRepository.findBySenderAndReceiver(
			recommended, dog);
		if (is_like && reverse_interacted != null && reverse_interacted.getIs_liked()) {
			Set<DogEntity> dogMatches = dog.getMatches();
			dogMatches.add(recommended);
			dog.setMatches(dogMatches);
			dogRepository.save(dog);

			return dogRepository.findDistance(
				recommended.getId(),
				recommended.getOwner().getLocation().getX(),
				recommended.getOwner().getLocation().getY(),
				dog.getOwner().getLocation().getX(),
				dog.getOwner().getLocation().getY());
		} else {
			return null;
		}
	}


	public void addInterest(String login, @Valid NewDogInterestDto interestDto)
		throws AlreadyExistsException, NotFoundException, IllegalLevelException {

		Set<ConstraintViolation<NewDogInterestDto>> violations = validator.validate(interestDto);
		if (!validator.validate(interestDto).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		InterestEntity interest = interestService.findById(interestDto.getInterestId());
		DogEntity dog = findByLogin(login);

		if (dog.getInterests().stream().anyMatch((x) -> x.getInterest().getId().equals(interest.getId()))) {
			throw new AlreadyExistsException(
				"У данной собаки уже существует такой интерес.");
		}

		DogsInterestsEntity interest_record = new DogsInterestsEntity(dog, interest,
			interestDto.getLevel());
		dogsInterestsRepository.save(interest_record);
		dogRepository.save(dog);
	}


}
