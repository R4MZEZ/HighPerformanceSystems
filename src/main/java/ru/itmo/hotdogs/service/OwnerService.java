package ru.itmo.hotdogs.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.hotdogs.exceptions.AccessDeniedException;
import ru.itmo.hotdogs.exceptions.NotEnoughMoneyException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.ShowDateException;
import ru.itmo.hotdogs.model.dto.NewShowDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.repository.OwnerRepository;

@Service
@RequiredArgsConstructor
public class OwnerService {


	private final OwnerRepository ownerRepository;
	private final ShowService showService;
	private final BreedService breedService;
	private final UserService userService;
	private final DogService dogService;

	public void save(OwnerEntity owner) {
		ownerRepository.save(owner);
	}

	public List<OwnerEntity> findAll() {
		return ownerRepository.findAll();
	}

	public OwnerEntity findByLogin(String login) throws NotFoundException {
		UserEntity user = userService.findByLogin(login);
		return ownerRepository.findByUser(user).orElseThrow(
			() -> new NotFoundException("Владельца с таким логином существует")
		);
	}

	@Transactional
	public void createShow(String login, NewShowDto newShowDto)
		throws NotFoundException, NotEnoughMoneyException {
		OwnerEntity owner = findByLogin(login);

		if (owner.getBalance() < newShowDto.getPrize()){
			throw new NotEnoughMoneyException();
		}

		Set<BreedEntity> allowedBreeds = new HashSet<>();
		for (Integer breedId : newShowDto.getAllowed_breeds()) {
			BreedEntity breed = breedService.findById(breedId)
				.orElseThrow(() -> new NotFoundException("Breed not found with id: " + breedId));
			allowedBreeds.add(breed);
		}

		owner.setBalance(owner.getBalance() - newShowDto.getPrize());
		owner.setReservedBalance(owner.getReservedBalance() + newShowDto.getPrize());
		ownerRepository.save(owner);

		ShowEntity show = new ShowEntity(newShowDto.getPrize(), newShowDto.getDate(), owner, allowedBreeds);
		showService.save(show);
	}

	@Transactional
	public DogEntity finishShow(String login, long showId, long winnerId)
		throws NotFoundException, AccessDeniedException, ShowDateException {
		OwnerEntity organizer = findByLogin(login);
		ShowEntity show = showService.findById(showId);

		if (!show.getOrganizer().equals(organizer)){
			throw new AccessDeniedException("Вы не можете завершить не свою выставку.");
		}

		DogEntity winner = dogService.findById(winnerId);

		if (!show.getParticipants().contains(winner)){
			throw new NotFoundException("Данной собаки нет в списке участников.");
		}

		if (new Date().before(show.getDate())){
			throw new ShowDateException("Вы не можете завершить выставку до ее начала.");
		}

		OwnerEntity owner = winner.getOwner();

		show.setWinner(winner);

		organizer.setReservedBalance(owner.getReservedBalance() - show.getPrize());
		owner.setBalance(owner.getBalance() + show.getPrize());

		showService.save(show);
		save(organizer);
		save(owner);

		return winner;
	}

}