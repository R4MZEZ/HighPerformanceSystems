package ru.itmo.hotdogs.model.dto;


import ru.itmo.hotdogs.model.entity.DogEntity;

/**
 * DTO for {@link DogEntity}
 */
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
//public record UserDto(String name, Integer age, BreedEntity breed, OwnerEntity owner,
//                      List<UsersInterestsEntity> userInterests, Set<UserEntity> userMatches,
//                      Set<UserEntity> userLikes) {
//
//}
public interface RecommendedDogDto {

	Long getId();

	String getName();

	Integer getAge();

	Double getDistance();

}