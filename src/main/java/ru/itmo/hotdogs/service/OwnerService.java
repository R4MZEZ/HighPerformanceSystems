package ru.itmo.hotdogs.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.hotdogs.exceptions.AccessDeniedException;
import ru.itmo.hotdogs.exceptions.AlreadyExistsException;
import ru.itmo.hotdogs.exceptions.NotEnoughMoneyException;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.ShowDateException;
import ru.itmo.hotdogs.model.dto.NewOwnerDto;
import ru.itmo.hotdogs.model.dto.NewShowDto;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.InterestEntity;
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
	private final Validator validator;
	private DogService dogService;

	@Autowired
	public void setDogService(DogService dogService) {
		this.dogService = dogService;
	}

	public OwnerEntity save(@Valid OwnerEntity owner) {
		Set<ConstraintViolation<OwnerEntity>> violations = validator.validate(owner);
		if (!validator.validate(owner).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		return ownerRepository.save(owner);
	}

	public NewOwnerDto createNewOwner(NewOwnerDto ownerUserDto) throws AlreadyExistsException {
		try {
			userService.findByLogin(ownerUserDto.getLogin());
			throw new AlreadyExistsException("Пользователь с указанным именем уже существует");
		} catch (NotFoundException ex) {
			List<String> roles = ownerUserDto.getIsOrganizer() ? List.of("ROLE_OWNER", "ROLE_ORGANIZER") : List.of("ROLE_OWNER");
			UserEntity user = userService.createNewUser(ownerUserDto, roles);
			GeometryFactory geometryFactory = new GeometryFactory();
			Coordinate coordinate = new Coordinate(ownerUserDto.getLatitude(), ownerUserDto.getLongitude());
			OwnerEntity owner = new OwnerEntity(user,
				ownerUserDto.getName(),
				ownerUserDto.getSurname(),
				ownerUserDto.getBalance(),
				geometryFactory.createPoint(coordinate));
			ownerRepository.save(owner);
			return new NewOwnerDto(
				ownerUserDto.getName(),
				ownerUserDto.getSurname(),
				ownerUserDto.getBalance(),
				ownerUserDto.getLatitude(),
				ownerUserDto.getLongitude(),
				ownerUserDto.getIsOrganizer());
		}
	}

	public Page<OwnerEntity> findAll(Pageable pageable) {
		return ownerRepository.findAll(pageable);
	}

	public OwnerEntity findByLogin(String login) throws NotFoundException {
		UserEntity user = userService.findByLogin(login);
		return ownerRepository.findByUser(user).orElseThrow(
			() -> new NotFoundException("Владельца с таким логином существует")
		);
	}

	@Transactional
	public void createShow(String login, @Valid NewShowDto newShowDto)
		throws NotFoundException, NotEnoughMoneyException, ConstraintViolationException {
		Set<ConstraintViolation<NewShowDto>> violations = validator.validate(newShowDto);
		if (!validator.validate(newShowDto).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		OwnerEntity owner = findByLogin(login);

		if (owner.getBalance() < newShowDto.getPrize()) {
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

		ShowEntity show = new ShowEntity(newShowDto.getPrize(), newShowDto.getDate(), owner,
			allowedBreeds);
		showService.save(show);
	}

	@Transactional
	public DogEntity finishShow(String login, long showId, long winnerId)
		throws NotFoundException, AccessDeniedException, ShowDateException {
		OwnerEntity organizer = findByLogin(login);
		ShowEntity show = showService.findById(showId);

		if (!show.getOrganizer().equals(organizer)) {
			throw new AccessDeniedException("Вы не можете завершить не свою выставку.");
		}

		DogEntity winner = dogService.findById(winnerId);

		if (!show.getParticipants().contains(winner)) {
			throw new NotFoundException("Данной собаки нет в списке участников.");
		}

		if (new Date().before(show.getDate())) {
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