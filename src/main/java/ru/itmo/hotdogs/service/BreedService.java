package ru.itmo.hotdogs.service;

import java.util.List;
import java.util.Optional;
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

	public Page<BreedEntity> findAll(Pageable pageable) {
		return breedRepository.findAll(pageable);
	}

	public Optional<BreedEntity> findById(Integer id) {
		return breedRepository.findById(id);
	}

	public BreedEntity findByName(String name) throws NotFoundException {
		return breedRepository.findByName(name)
			.orElseThrow(() -> new NotFoundException("Породы с таким названием не существует"));
	}

	public BreedEntity createBreed(BreedEntity breed){ return breedRepository.save(breed); }
}