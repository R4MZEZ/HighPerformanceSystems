package ru.itmo.ownerservice.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.ownerservice.exceptions.AlreadyExistsException;
import ru.itmo.ownerservice.exceptions.BreedNotAllowedException;
import ru.itmo.ownerservice.exceptions.CheatingException;
import ru.itmo.ownerservice.exceptions.NotFoundException;
import ru.itmo.ownerservice.exceptions.ShowDateException;
import ru.itmo.ownerservice.model.dto.ResponseDto;
import ru.itmo.ownerservice.model.dto.ShowDtoRequest;
import ru.itmo.ownerservice.model.entity.BreedEntity;
import ru.itmo.ownerservice.model.entity.DogEntity;
import ru.itmo.ownerservice.model.entity.OwnerEntity;
import ru.itmo.ownerservice.model.entity.ShowEntity;
import ru.itmo.ownerservice.repository.ShowRepository;
import ru.itmo.ownerservice.rest.DogsApi;

@Service
@RequiredArgsConstructor
public class ShowService {

	private final ShowRepository showRepository;
	private final Validator validator;
	private final DogsApi dogsApi;
	public ShowEntity saveShow(ShowEntity show) {
		return show == null ? null : showRepository.save(show);
	}

	public void deleteAll(){ showRepository.deleteAll(); }

	public ShowEntity findById(Long id) throws NotFoundException {
		return showRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Выставки с таким id не существует"));
	}

	@Transactional
	public ShowEntity addParticipant(ShowEntity show, DogEntity dog)
		throws AlreadyExistsException, CheatingException, BreedNotAllowedException, ShowDateException {
		if (new Date().after(show.getDate())) {
			throw new ShowDateException("Выставка уже прошла");
		}

		if (!show.getAllowedBreeds().contains(dog.getBreed())) {
			throw new BreedNotAllowedException();
		}

		if (show.getParticipants().contains(dog)) {
			throw new AlreadyExistsException("Вы уже участвуете в этой выставке");
		}

		if (show.getOrganizer().getId().equals(dog.getOwner().getId())) {
			throw new CheatingException();
		}

		List<DogEntity> participants = show.getParticipants();
		participants.add(dog);
		show.setParticipants(participants);
		return showRepository.save(show);
	}

	@Transactional
	public ShowEntity createShow(OwnerEntity owner, @Valid ShowDtoRequest newShowDto)
		throws ConstraintViolationException, NotFoundException {

		Set<ConstraintViolation<ShowDtoRequest>> violations = validator.validate(newShowDto);
		if (!validator.validate(newShowDto).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		Set<BreedEntity> allowedBreeds = new HashSet<>();
		for (String breedName : newShowDto.getAllowed_breeds()) {
			ResponseDto<BreedEntity> breedResponse = dogsApi.findBreedByName(breedName);
			if (breedResponse.code() == HttpStatus.NOT_FOUND)
				throw new NotFoundException(breedResponse.error().getMessage());
			allowedBreeds.add(breedResponse.body());
		}

		ShowEntity show = new ShowEntity(newShowDto.getPrize(), newShowDto.getDate(), owner,allowedBreeds);
		return saveShow(show);
	}

}
