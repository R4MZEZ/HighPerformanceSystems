package ru.itmo.hotdogs.service;

import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.dto.NewShowDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.repository.ShowRepository;

@Service
@RequiredArgsConstructor
public class ShowService {

	private final ShowRepository showRepository;
	private final Validator validator;
	private final BreedService breedService;

	public void save(@Valid ShowEntity show) throws ConstraintViolationException {
		Set<ConstraintViolation<ShowEntity>> violations = validator.validate(show);
		if (!validator.validate(show).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		showRepository.save(show);
	}

	public ShowEntity findById(Long id) throws NotFoundException {
		return showRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Выставки с таким id не существует"));
	}

	@Transactional
	public void addParticipant(ShowEntity show, DogEntity dog) {
		Set<DogEntity> participants = show.getParticipants();
		participants.add(dog);
		show.setParticipants(participants);
		showRepository.save(show);
	}

	@Transactional
	public void createShow(OwnerEntity owner, @Valid NewShowDto newShowDto)
		throws ConstraintViolationException, NotFoundException {

		Set<ConstraintViolation<NewShowDto>> violations = validator.validate(newShowDto);
		if (!validator.validate(newShowDto).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		Set<BreedEntity> allowedBreeds = new HashSet<>();
		for (Integer breedId : newShowDto.getAllowed_breeds()) {
			BreedEntity breed = breedService.findById(breedId)
				.orElseThrow(() -> new NotFoundException("Breed not found with id: " + breedId));
			allowedBreeds.add(breed);
		}

		ShowEntity show = new ShowEntity(newShowDto.getPrize(), newShowDto.getDate(), owner,allowedBreeds);
		save(show);
	}

}
