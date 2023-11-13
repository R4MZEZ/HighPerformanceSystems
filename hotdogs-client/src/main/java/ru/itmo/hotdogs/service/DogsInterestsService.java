package ru.itmo.hotdogs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInteractionsEntity;
import ru.itmo.hotdogs.model.entity.DogsInterestsEntity;
import ru.itmo.hotdogs.repository.DogsInteractionsRepository;
import ru.itmo.hotdogs.repository.DogsInterestsRepository;

@Service
@RequiredArgsConstructor
public class DogsInterestsService {

	private final DogsInterestsRepository dogsInterestsRepository;

	public DogsInterestsEntity save(DogsInterestsEntity dogsInterestsEntity){
		return dogsInterestsRepository.save(dogsInterestsEntity);
	}

}
