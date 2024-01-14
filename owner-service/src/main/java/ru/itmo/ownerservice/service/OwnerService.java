package ru.itmo.ownerservice.service;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.ownerservice.exceptions.AccessDeniedException;
import ru.itmo.ownerservice.exceptions.AlreadyExistsException;
import ru.itmo.ownerservice.exceptions.BreedNotAllowedException;
import ru.itmo.ownerservice.exceptions.CheatingException;
import ru.itmo.ownerservice.exceptions.NotEnoughMoneyException;
import ru.itmo.ownerservice.exceptions.NotFoundException;
import ru.itmo.ownerservice.exceptions.ShowDateException;
import ru.itmo.ownerservice.model.dto.OwnerDto;
import ru.itmo.ownerservice.model.dto.ResponseDto;
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
	private final ShowService showService;

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
		throws AlreadyExistsException, ConstraintViolationException, ExecutionException, InterruptedException {
		Set<ConstraintViolation<OwnerDto>> violations = validator.validate(ownerUserDto);
		if (!validator.validate(ownerUserDto).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		Set<Integer> roles = ownerUserDto.getIsOrganizer() ? Set.of(2, 4)
			: Set.of(2);
		userDto.setRoles(roles);

		ResponseDto<UserEntity> response = userApi.createNewUser(userDto).toFuture().get();
		if (!response.code().is2xxSuccessful()){
			throw new AlreadyExistsException(response.error().getMessage());
		}
		response = userApi.findByLogin(userDto.getLogin()).toFuture().get();
		for (Integer roleId : userDto.getRoles()){
			userApi.addRole(response.body().getId(), roleId);
		}

		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate coordinate = new Coordinate(ownerUserDto.getLatitude(),
			ownerUserDto.getLongitude());
		OwnerEntity owner = new OwnerEntity(response.body(),
			ownerUserDto.getName(),
			ownerUserDto.getSurname(),
			ownerUserDto.getBalance(),
			geometryFactory.createPoint(coordinate));
		return ownerRepository.save(owner);

	}

	public Page<OwnerEntity> findAll(Pageable pageable) {
		return ownerRepository.findAll(pageable);
	}

	public Optional<OwnerEntity> findByLogin(String login)
		throws ExecutionException, InterruptedException {
			ResponseDto<UserEntity> userResponse = userApi.findByLogin(login).toFuture().get();
			if (userResponse.code().is2xxSuccessful())
				return ownerRepository.findByUser(userResponse.body());
			else
				return Optional.empty();
	}

	@Transactional
	public ShowEntity createShow(String login, @Valid ShowDtoRequest newShowDto)
		throws NotFoundException, NotEnoughMoneyException, ConstraintViolationException, ExecutionException, InterruptedException {
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
		throws NotFoundException, AccessDeniedException, ShowDateException, AlreadyExistsException, ExecutionException, InterruptedException {
		OwnerEntity organizer = findByLogin(login).orElseThrow(() -> new NotFoundException("Владельца с таким логином не существует"));

		ShowEntity show = showService.findById(showId);

		if (!show.getOrganizer().getId().equals(organizer.getId())) {
			throw new AccessDeniedException("Вы не можете завершить не свою выставку.");
		}

		if (show.getWinner() != null) {
			throw new AlreadyExistsException("Выставка уже завершена");
		}

		ResponseDto<DogEntity> winner = dogsApi.findById(winnerId);

		if (winner.code() == HttpStatus.NOT_FOUND)
			throw new NotFoundException("Собаки с таким ID не существует");

		if (!show.getParticipants().contains(winner.body())) {
			throw new NotFoundException("Данной собаки нет в списке участников.");
		}

		if (new Date().before(show.getDate())) {
			throw new ShowDateException("Вы не можете завершить выставку до ее начала.");
		}

		OwnerEntity owner = ownerRepository.findById(winner.body().getOwner().getId()).orElseThrow(
			() -> new NotFoundException("Хозяин собаки не найден"));

		show.setWinner(winner.body());

		organizer.setReservedBalance(organizer.getReservedBalance() - show.getPrize());
		owner.setBalance(owner.getBalance() + show.getPrize());

		showService.saveShow(show);
		save(organizer);
		save(owner);

		return winner.body();
	}

	public ShowEntity addParticipant(Long showId, DogEntity dog)
		throws NotFoundException, CheatingException, AlreadyExistsException, BreedNotAllowedException, ShowDateException {
		ShowEntity show = showService.findById(showId);
		return showService.addParticipant(show, dog);
	}

}