package ru.itmo.hotdogs.utils;


import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.itmo.hotdogs.model.dto.NewDogDto;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;

@Component
public class DtoConverter {
	public static NewDogDto dogEntityToDto(DogEntity dog){
		return new NewDogDto(
			dog.getName(),
			dog.getAge(),
			dog.getBreed().getName(),
			dog.getOwner().getUser().getLogin(),
			dog.getInterests().stream().collect(Collectors.toMap(
				interest -> interest.getInterest().getName(),
				DogsInterestsEntity::getLevel)));
	}
}
