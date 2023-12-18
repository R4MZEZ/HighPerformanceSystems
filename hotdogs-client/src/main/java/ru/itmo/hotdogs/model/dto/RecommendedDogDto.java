package ru.itmo.hotdogs.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.hotdogs.model.entity.DogEntity;

/**
 * DTO for {@link DogEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedDogDto implements RecommendedDog{

	Long id;

	String name;

	Integer age;

	Double distance;

}