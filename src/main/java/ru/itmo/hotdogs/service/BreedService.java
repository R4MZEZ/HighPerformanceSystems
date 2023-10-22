package ru.itmo.hotdogs.service;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.repository.BreedRepository;

@Service
@RequiredArgsConstructor
public class BreedService {


	private final BreedRepository breedRepository;
	private final Validator validator;

	public Page<BreedEntity> findAll(Pageable pageable) {
		return breedRepository.findAll(pageable);
	}

	public BreedEntity findByName(String name) throws NotFoundException {
		return breedRepository.findByName(name)
			.orElseThrow(() -> new NotFoundException("Породы с таким названием не существует"));
	}

	public BreedEntity createBreed(@Valid BreedEntity breed) throws ConstraintViolationException{
		Set<ConstraintViolation<BreedEntity>> violations = validator.validate(breed);
		if (!validator.validate(breed).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		return breedRepository.save(breed);
	}

	public void deleteAll(){
		breedRepository.deleteAll();
	}
}