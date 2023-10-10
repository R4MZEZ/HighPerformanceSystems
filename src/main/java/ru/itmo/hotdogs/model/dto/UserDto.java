package ru.itmo.hotdogs.model.dto;

//import com.fasterxml.jackson.databind.PropertyNamingStrategies;
//import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.UserEntity;

/**
 * DTO for {@link UserEntity}
 */
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
//public record UserDto(String name, Integer age, BreedEntity breed, OwnerEntity owner,
//                      List<UsersInterestsEntity> userInterests, Set<UserEntity> userMatches,
//                      Set<UserEntity> userLikes) {
//
//}
@Data
public class UserDto {
  private String username;
  private Integer age;

  public UserDto(String username, Integer age) {
    this.username = username;
    this.age = age;
  }
}