package ru.itmo.hotdogs.model.dto;


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
public interface RecommendedUserDto {

	Long getId();

	String getUsername();

	Integer getAge();

	Double getDistance();

}