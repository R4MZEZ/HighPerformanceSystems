package ru.itmo.hotdogs.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import java.util.Set;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.model.entity.UsersInterestsEntity;

/**
 * DTO for {@link UserEntity}
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserDto(String name, Integer age, BreedEntity breed, OwnerEntity owner,
                      List<UsersInterestsEntity> userInterests, Set<UserEntity> userMatches,
                      Set<UserEntity> userLikes) {

}