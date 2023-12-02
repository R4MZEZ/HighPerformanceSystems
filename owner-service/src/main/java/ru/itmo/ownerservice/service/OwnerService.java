package ru.itmo.ownerservice.service;

import java.util.Date;
import java.util.Optional;
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
import ru.itmo.ownerservice.exceptions.AccessDeniedException;
import ru.itmo.ownerservice.exceptions.AlreadyExistsException;
import ru.itmo.ownerservice.exceptions.NotEnoughMoneyException;
import ru.itmo.ownerservice.exceptions.NotFoundException;
import ru.itmo.ownerservice.exceptions.ShowDateException;
import ru.itmo.ownerservice.model.dto.OwnerDto;
import ru.itmo.ownerservice.model.dto.ShowDtoRequest;
import ru.itmo.ownerservice.model.dto.UserDto;
import ru.itmo.ownerservice.model.entity.DogEntity;
import ru.itmo.ownerservice.model.entity.OwnerEntity;
import ru.itmo.ownerservice.model.entity.ShowEntity;
import ru.itmo.ownerservice.model.entity.UserEntity;
import ru.itmo.ownerservice.repository.OwnerRepository;
import ru.itmo.ownerservice.rest.DogsApi;
import ru.itmo.ownerservice.rest.UserApi;

@Service
@RequiredArgsConstructor
public class OwnerService {


	private final OwnerRepository ownerRepository;
	private final Validator validator;
	private final UserApi userApi;
	private final DogsApi dogsApi;
	private ShowService showService;

	@Autowired
	public void setShowService(ShowService showService) {
		this.showService = showService;
	}
	public void deleteAll() {
		ownerRepository.deleteAll();
	}


	public void save(@Valid OwnerEntity owner) {
		Set<ConstraintViolation<OwnerEntity>> violations = validator.validate(owner);
		if (!validator.validate(owner).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		ownerRepository.save(owner);
	}

	public OwnerEntity createNewOwner(@Valid UserDto userDto, @Valid OwnerDto ownerUserDto)
		throws AlreadyExistsException, ConstraintViolationException {
		Set<ConstraintViolation<OwnerDto>> violations = validator.validate(ownerUserDto);
		if (!validator.validate(ownerUserDto).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		Set<String> roles = ownerUserDto.getIsOrganizer() ? Set.of("ROLE_OWNER", "ROLE_ORGANIZER")
			: Set.of("ROLE_OWNER");
		userDto.setRoles(roles);
		UserEntity user = userApi.createNewUser(userDto);

		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate coordinate = new Coordinate(ownerUserDto.getLatitude(),
			ownerUserDto.getLongitude());
		OwnerEntity owner = new OwnerEntity(user,
			ownerUserDto.getName(),
			ownerUserDto.getSurname(),
			ownerUserDto.getBalance(),
			geometryFactory.createPoint(coordinate));
		return ownerRepository.save(owner);

	}

	public Page<OwnerEntity> findAll(Pageable pageable) {
		return ownerRepository.findAll(pageable);
	}

	public Optional<OwnerEntity> findByLogin(String login){
		try {
			UserEntity user = userApi.findByLogin(login);
			return ownerRepository.findByUser(user);
		} catch (NotFoundException e) {
			return Optional.empty();
		}

	}

	@Transactional
	public ShowEntity createShow(String login, @Valid ShowDtoRequest newShowDto)
		throws NotFoundException, NotEnoughMoneyException, ConstraintViolationException {
		OwnerEntity owner = findByLogin(login).orElseThrow(() -> new NotFoundException("Владельца с таким логином не существует"));

		if (owner.getBalance() < newShowDto.getPrize()) {
			throw new NotEnoughMoneyException();
		}

		ShowEntity show = showService.createShow(owner, newShowDto);

		owner.setBalance(owner.getBalance() - newShowDto.getPrize());
		owner.setReservedBalance(owner.getReservedBalance() + newShowDto.getPrize());
		ownerRepository.save(owner);
		return show;
	}

	@Transactional
	public DogEntity finishShow(String login, long showId, long winnerId)
		throws NotFoundException, AccessDeniedException, ShowDateException {
		OwnerEntity organizer = findByLogin(login).orElseThrow(() -> new NotFoundException("Владельца с таким логином не существует"));

		ShowEntity show = showService.findById(showId);

		if (!show.getOrganizer().getId().equals(organizer.getId())) {
			throw new AccessDeniedException("Вы не можете завершить не свою выставку.");
		}

		DogEntity winner = dogsApi.findById(winnerId);

		if (!show.getParticipants().contains(winner)) {
			throw new NotFoundException("Данной собаки нет в списке участников.");
		}

		if (new Date().before(show.getDate())) {
			throw new ShowDateException("Вы не можете завершить выставку до ее начала.");
		}

		OwnerEntity owner = winner.getOwner();

		show.setWinner(winner);

		organizer.setReservedBalance(organizer.getReservedBalance() - show.getPrize());
		owner.setBalance(owner.getBalance() + show.getPrize());

		showService.save(show);
		save(organizer);
		save(owner);

		return winner;
	}

}