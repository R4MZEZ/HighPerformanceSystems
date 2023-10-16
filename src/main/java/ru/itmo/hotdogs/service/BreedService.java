package ru.itmo.hotdogs.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.repository.BreedRepository;

@Service
@RequiredArgsConstructor
public class BreedService {


	private final BreedRepository breedRepository;

	public List<BreedEntity> findAll() {
		return breedRepository.findAll();
	}
}