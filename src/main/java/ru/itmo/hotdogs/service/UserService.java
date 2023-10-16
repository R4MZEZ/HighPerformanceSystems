package ru.itmo.hotdogs.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
			.name(recommendedUserDto.getUsername())
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
		Optional<UserEntity> user = userRepository.findById(id);
		if (user.isPresent()) {
			RecommendedUserDto recommendedUser = userRepository.findNearest(
				user.get().getOwner().getLocation().getX(),
				user.get().getOwner().getLocation().getY()).get(0);
			user.get().setCurRecommended(userRepository.findById(recommendedUser.getId()).get());
			userRepository.save(user.get());
			return recommendedUser;
		} else {
			throw new NotFoundException("Пользователя с таким id не существует");
		}
	}

	public RecommendedUserDto rateRecommended(Long id, boolean is_like)
		throws NotFoundException, NullRecommendationException {
		Optional<UserEntity> user = userRepository.findById(id);
		if (user.isPresent()) {
			UserEntity recommended = user.get().getCurRecommended();
			if (recommended == null) {
				throw new NullRecommendationException("Сначала необходимо получить рекомендацию");
			}
			UsersInteractionsEntity interaction_record = new UsersInteractionsEntity(user.get(),
				recommended, is_like);
			usersInteractionsRepository.save(interaction_record);

			user.get().setCurRecommended(null);
			userRepository.save(user.get());

			UsersInteractionsEntity reverse_interacted = usersInteractionsRepository.findBySenderAndReceiver(
				recommended, user.get());
			if (is_like && reverse_interacted != null && reverse_interacted.getIs_liked()) {
				return userRepository.findDistance(
					recommended.getId(),
					recommended.getOwner().getLocation().getX(),
					recommended.getOwner().getLocation().getY(),
					user.get().getOwner().getLocation().getX(),
					user.get().getOwner().getLocation().getY());
			} else {
				return null;
			}
		} else {
			throw new NotFoundException("Пользователя с таким id не существует");
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
		user.getInterests().add(interest_record);
		user.getInterests().sort(Comparator.comparing(UsersInterestsEntity::getLevel));

		usersInterestsRepository.save(interest_record);
		userRepository.save(user);
	}


}
