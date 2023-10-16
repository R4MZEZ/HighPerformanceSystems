package ru.itmo.hotdogs.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.IllegalLevelException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.NullRecommendationException;
import ru.itmo.hotdogs.model.dto.RecommendedUserDto;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.model.entity.UsersInteractionsEntity;
import ru.itmo.hotdogs.model.entity.UsersInterestsEntity;
import ru.itmo.hotdogs.repository.InterestRepository;
import ru.itmo.hotdogs.repository.UserRepository;
import ru.itmo.hotdogs.repository.UsersInteractionsRepository;
import ru.itmo.hotdogs.repository.UsersInterestsRepository;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UsersInteractionsRepository usersInteractionsRepository;
	private final InterestRepository interestRepository;
	private final UsersInterestsRepository usersInterestsRepository;

	public List<UserEntity> findAll() {
		return userRepository.findAll();
	}

	public UserEntity findById(long id) throws NotFoundException {
		Optional<UserEntity> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) {
			throw new NotFoundException("Пользователя с таким id не существует");
		}

		return userOptional.get();
	}

	public UserEntity save(RecommendedUserDto recommendedUserDto) {
		var user = UserEntity.builder()
			.name(recommendedUserDto.getName())
			.age(recommendedUserDto.getAge())
			.build();
		return userRepository.save(user);
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


	public RecommendedUserDto findNearest(Long id) throws NotFoundException {
		Optional<UserEntity> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) {
			throw new NotFoundException("Пользователя с таким id не существует");
		}
		UserEntity user = userOptional.get();
		RecommendedUserDto recommendedUser = userRepository.findNearest(
			user.getOwner().getLocation().getX(),
			user.getOwner().getLocation().getY(),
			user.getId());
		if (recommendedUser == null) {
			throw new NotFoundException("Ты посмотрел всех, приходи позже");
		}
		user.setCurRecommended(userRepository.findById(recommendedUser.getId()).get());
		userRepository.save(user);
		return recommendedUser;

	}

	/**
	 * @return возвращает экземпляр RecommendedUserDto, если был достигнут мэтч, иначе - null
	 */
	public RecommendedUserDto rateRecommended(Long id, boolean is_like)
		throws NotFoundException, NullRecommendationException {
		Optional<UserEntity> userOptional = userRepository.findById(id);
		if (userOptional.isEmpty()) {
			throw new NotFoundException("Пользователя с таким id не существует");
		}
		UserEntity user = userOptional.get();
		UserEntity recommended = user.getCurRecommended();
		if (recommended == null) {
			throw new NullRecommendationException("Сначала необходимо получить рекомендацию");
		}
		UsersInteractionsEntity interaction_record = new UsersInteractionsEntity(user, recommended,
			is_like);
		usersInteractionsRepository.save(interaction_record);

		user.setCurRecommended(null);
		userRepository.save(user);

		UsersInteractionsEntity reverse_interacted = usersInteractionsRepository.findBySenderAndReceiver(
			recommended, user);
		if (is_like && reverse_interacted != null && reverse_interacted.getIs_liked()) {
			Set<UserEntity> user_matches = user.getMatches();
			user_matches.add(recommended);
			user.setMatches(user_matches);
			userRepository.save(user);

			return userRepository.findDistance(
				recommended.getId(),
				recommended.getOwner().getLocation().getX(),
				recommended.getOwner().getLocation().getY(),
				user.getOwner().getLocation().getX(),
				user.getOwner().getLocation().getY());
		} else {
			return null;
		}
	}


	public void addInterest(long userId, int interestId, int level)
		throws AlreadyExistsException, NotFoundException, IllegalLevelException {
		if (level < 1 || level > 10) {
			throw new IllegalLevelException("Значение level должно быть в интервале (0;10].");
		}

		Optional<InterestEntity> interestOptional = interestRepository.findById(interestId);
		if (interestOptional.isEmpty()) {
			throw new NotFoundException("Интереса с таким id не существует.");
		}

		UserEntity user = findById(userId);
		InterestEntity interest = interestOptional.get();

		if (user.getInterests().stream().anyMatch((x) -> x.getInterest().equals(interest))) {
			throw new AlreadyExistsException(
				"У данного пользователя уже существует такой интерес.");
		}

		UsersInterestsEntity interest_record = new UsersInterestsEntity(user, interest, level);
//		List<UsersInterestsEntity> userInterests = user.getInterests();
//		userInterests.add(interest_record);
//		userInterests.sort(Comparator.comparing(UsersInterestsEntity::getLevel));
//		user.setInterests(userInterests);

		usersInterestsRepository.save(interest_record);
		userRepository.save(user);
	}


}
