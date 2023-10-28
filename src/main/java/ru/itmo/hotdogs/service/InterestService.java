package ru.itmo.hotdogs.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.repository.InterestRepository;

@Service
@RequiredArgsConstructor
public class InterestService {

	private final Validator validator;

	private final InterestRepository interestRepository;

	public List<InterestEntity> findAll() {
		return interestRepository.findAll();
	}

	public void deleteAll() { interestRepository.deleteAll(); }

	public Optional<InterestEntity> deleteByName(String name){ return interestRepository.deleteByName(name); }

	public InterestEntity save(@Valid InterestEntity interest) throws ConstraintViolationException{
		Set<ConstraintViolation<InterestEntity>> violations = validator.validate(interest);
		if (!validator.validate(interest).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		return interestRepository.save(interest);
	}

	public InterestEntity findById(Integer id) throws NotFoundException {
		return interestRepository.findById(id).orElseThrow(
			() -> new NotFoundException("Интереса с таким id не существует.")
		);
	}

	public InterestEntity findByName(String name) throws NotFoundException {
		return interestRepository.findByName(name)
			.orElseThrow(() -> new NotFoundException("Интереса с названием %s не существует".formatted(name)));
	}
}