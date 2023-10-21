package ru.itmo.hotdogs.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.repository.InterestRepository;

@Service
@RequiredArgsConstructor
public class InterestService {


	private final InterestRepository interestRepository;

	public List<InterestEntity> findAll() {
		return interestRepository.findAll();
	}

	public void save(InterestEntity interest) {
		interestRepository.save(interest);
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