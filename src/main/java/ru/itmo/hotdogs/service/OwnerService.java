package ru.itmo.hotdogs.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.exceptions.AccessDeniedException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.dto.ShowDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.repository.OwnerRepository;

@Service
@RequiredArgsConstructor
public class OwnerService {


	private final OwnerRepository ownerRepository;
	private final ShowService showService;
	private final BreedService breedService;

	public void save(OwnerEntity owner) {
		ownerRepository.save(owner);
	}

	public List<OwnerEntity> findAll() {
		return ownerRepository.findAll();
	}

	public void createShow(Long id, ShowDto showDto) throws NotFoundException {
		OwnerEntity owner = ownerRepository.findById(id).orElseThrow(
			() -> new NotFoundException("Владельца с таким id не существует")
		);

		Set<BreedEntity> allowedBreeds = new HashSet<>();
		for (Integer breedId : showDto.getAllowed_breeds()) {
			BreedEntity breed = breedService.findById(breedId)
				.orElseThrow(() -> new NotFoundException("Breed not found with id: " + breedId));
			allowedBreeds.add(breed);
		}

		ShowEntity show = new ShowEntity(showDto.getPrize(), showDto.getDate(), owner, allowedBreeds);
		showService.save(show);
	}

}