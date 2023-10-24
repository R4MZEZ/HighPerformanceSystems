package ru.itmo.hotdogs.utils;


import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.itmo.hotdogs.model.dto.DogDto;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;

@Component
public class DtoConverter {
	public static DogDto dogEntityToDto(DogEntity dog){
		return new DogDto(
			dog.getName(),
			dog.getAge(),
			dog.getBreed().getName(),
			dog.getOwner().getUser().getLogin(),
			dog.getInterests().stream().collect(Collectors.toMap(
				interest -> interest.getInterest().getName(),
				DogsInterestsEntity::getLevel)));
	}
}
