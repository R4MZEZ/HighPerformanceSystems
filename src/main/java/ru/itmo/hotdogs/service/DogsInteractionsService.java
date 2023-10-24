package ru.itmo.hotdogs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.DogsInteractionsEntity;
import ru.itmo.hotdogs.repository.DogsInteractionsRepository;

@Service
@RequiredArgsConstructor
public class DogsInteractionsService {

	private final DogsInteractionsRepository dogsInteractionsRepository;

	public DogsInteractionsEntity findBySenderAndReceiver(DogEntity sender, DogEntity receiver) {
		return dogsInteractionsRepository.findBySenderAndReceiver(sender, receiver);
	}

	public DogsInteractionsEntity save(DogsInteractionsEntity dogsInteractionsEntity){
		return dogsInteractionsRepository.save(dogsInteractionsEntity);
	}

}
