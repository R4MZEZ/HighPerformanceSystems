package ru.itmo.hotdogs.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.model.dto.RecommendedUserDto;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

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


  public RecommendedUserDto findAround(Long id) throws NotFoundException {
    Optional<UserEntity> user = userRepository.findById(id);
    if (user.isPresent()) {
      RecommendedUserDto recommendedUser = userRepository.findNearest(
          user.get().getOwner().getLocation().getX(),
          user.get().getOwner().getLocation().getY()).get(0);
      user.get().setCurRecommended(userRepository.findById(recommendedUser.getId()).get());
      return recommendedUser;
    } else {
      throw new NotFoundException();
    }
  }


}
