package ru.itmo.hotdogs.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.repository.ShowRepository;

@Service
@RequiredArgsConstructor
public class ShowService {

	private final ShowRepository showRepository;

	public void save(ShowEntity show) {
		showRepository.save(show);
	}

	public ShowEntity findById(Long id) throws NotFoundException {
		return showRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("Выставки с таким id не существует"));
	}

	@Transactional
	public void addParticipant(ShowEntity show, DogEntity dog){
		Set<DogEntity> participants = show.getParticipants();
		participants.add(dog);
		show.setParticipants(participants);
		showRepository.save(show);
	}

}
