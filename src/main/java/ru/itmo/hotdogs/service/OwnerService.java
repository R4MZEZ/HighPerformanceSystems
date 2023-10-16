package ru.itmo.hotdogs.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.swing.text.html.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.exceptions.AccessDeniedException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.dto.ShowDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.repository.BreedRepository;
import ru.itmo.hotdogs.repository.OwnerRepository;
import ru.itmo.hotdogs.repository.ShowRepository;

@Service
@RequiredArgsConstructor
public class OwnerService {


	private final OwnerRepository ownerRepository;
	private final ShowRepository showRepository;
	private final BreedRepository breedRepository;

	public OwnerEntity save(OwnerEntity owner) {
		return ownerRepository.save(owner);
	}

	public List<OwnerEntity> findAll() {
		return ownerRepository.findAll();
	}

	public void createShow(Long id, ShowDto showDto) throws NotFoundException, AccessDeniedException {
		Optional<OwnerEntity> ownerOptional = ownerRepository.findById(id);
		if (ownerOptional.isEmpty())
			throw new NotFoundException("Владельца с таким id не существует");
		OwnerEntity owner = ownerOptional.get();

		if (!owner.getIs_organizer())
			throw new AccessDeniedException("Вы не организатор");

		Set<BreedEntity> allowedBreeds = new HashSet<>();
		for (Integer breedId : showDto.getAllowed_breeds()) {
			BreedEntity breed = breedRepository.findById(breedId)
				.orElseThrow(() -> new NotFoundException("Breed not found with id: " + breedId));
			allowedBreeds.add(breed);
		}

		ShowEntity show = new ShowEntity(showDto.getPrize(), showDto.getDate(), owner, allowedBreeds);
		showRepository.save(show);
	}

}