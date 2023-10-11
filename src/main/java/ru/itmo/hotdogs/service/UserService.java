package ru.itmo.hotdogs.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.model.dto.RecommendedUserDto;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.model.entity.UsersInteractionsEntity;
import ru.itmo.hotdogs.repository.UserRepository;
import ru.itmo.hotdogs.repository.UsersInteractionsRepository;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UsersInteractionsRepository usersInteractionsRepository;

  public List<UserEntity> findAll() {
    return userRepository.findAll();
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
      throw new NotFoundException();
    }
  }

  public RecommendedUserDto rateRecommended(Long id, boolean is_like)
      throws NotFoundException, NullPointerException {
    Optional<UserEntity> user = userRepository.findById(id);
    if (user.isPresent()) {
      UserEntity recommended = user.get().getCurRecommended();
      if (recommended == null) {
        throw new NullPointerException();
      }
      UsersInteractionsEntity interaction_record = new UsersInteractionsEntity(user.get(), recommended, is_like);
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
      throw new NotFoundException();
    }
  }


}
