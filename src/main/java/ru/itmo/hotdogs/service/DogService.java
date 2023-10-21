package ru.itmo.hotdogs.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.IllegalLevelException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.NullRecommendationException;
import ru.itmo.hotdogs.model.dto.RecommendedDogDto;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInteractionsEntity;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.repository.InterestRepository;
import ru.itmo.hotdogs.repository.DogRepository;
import ru.itmo.hotdogs.repository.DogsInteractionsRepository;
import ru.itmo.hotdogs.repository.DogsInterestsRepository;

@Service
@RequiredArgsConstructor
public class DogService {

	private final DogRepository dogRepository;
	private final DogsInteractionsRepository dogsInteractionsRepository;
	private final InterestRepository interestRepository;
	private final DogsInterestsRepository dogsInterestsRepository;
	private final UserService userService;

	public List<DogEntity> findAll() {
		return dogRepository.findAll();
	}


	public DogEntity findByLogin(String login) throws NotFoundException {
		UserEntity user = userService.findByLogin(login);
		return dogRepository.findByUser(user).orElseThrow(
			() -> new NotFoundException("Собаки с таким логином не существует")
		);
	}

	public DogEntity save(RecommendedDogDto recommendedDogDto) {
		var dog = DogEntity.builder()
			.name(recommendedDogDto.getName())
			.age(recommendedDogDto.getAge())
			.build();
		return dogRepository.save(dog);
	}
//  }  public UserEntity save(UserDto userDto) {
//    var user = UserEntity.builder()
//        .name(userDto.name())
//        .age(userDto.age())
//        .breed(userDto.breed())
//        .owner(userDto.owner())
//        .userInterests(userDto.userInterests())
//        .userLikes(userDto.userLikes())
//        .userMatches(userDto.userMatches())
//        .build();
//    return userRepository.save(user);
//  }


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
			throw new NullRecommendationException("Сначала необходимо получить рекомендацию");
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


	public void addInterest(String login, int interestId, int level)
		throws AlreadyExistsException, NotFoundException, IllegalLevelException {
		if (level < 1 || level > 10) {
			throw new IllegalLevelException("Значение level должно быть в интервале (0;10].");
		}

		InterestEntity interest = interestRepository.findById(interestId).orElseThrow(
			() -> new NotFoundException("Интереса с таким id не существует."));

		DogEntity dog = findByLogin(login);

		if (dog.getInterests().stream().anyMatch((x) -> x.getInterest().equals(interest))) {
			throw new AlreadyExistsException(
				"У данной собаки уже существует такой интерес.");
		}

		DogsInterestsEntity interest_record = new DogsInterestsEntity(dog, interest, level);
//		List<UsersInterestsEntity> userInterests = user.getInterests();
//		userInterests.add(interest_record);
//		userInterests.sort(Comparator.comparing(UsersInterestsEntity::getLevel));
//		user.setInterests(userInterests);

		dogsInterestsRepository.save(interest_record);
		dogRepository.save(dog);
	}


}
