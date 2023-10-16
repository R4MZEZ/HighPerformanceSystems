package ru.itmo.hotdogs.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.repository.OwnerRepository;

@Service
@RequiredArgsConstructor
public class OwnerService {


	private final OwnerRepository ownerRepository;

	public OwnerEntity save(OwnerEntity owner) {
		return ownerRepository.save(owner);
	}

	public List<OwnerEntity> findAll() {
		return ownerRepository.findAll();
	}
}