package ru.itmo.hotdogs.service;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.repository.ShowRepository;

@Service
@RequiredArgsConstructor
public class ShowService {

	private final ShowRepository showRepository;
	private final Validator validator;

	public void save(@Valid ShowEntity show) throws ConstraintViolationException{
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
	public void addParticipant(ShowEntity show, DogEntity dog){
		Set<DogEntity> participants = show.getParticipants();
		participants.add(dog);
		show.setParticipants(participants);
		showRepository.save(show);
	}

}
