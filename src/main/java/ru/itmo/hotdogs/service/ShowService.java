package ru.itmo.hotdogs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.repository.ShowRepository;

@Service
@RequiredArgsConstructor
public class ShowService {

	ShowRepository showRepository;

	public ShowEntity save(ShowEntity show){ return showRepository.save(show); }

}
