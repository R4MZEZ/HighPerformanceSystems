package ru.itmo.hotdogs.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.repository.InterestRepository;

@Service
@RequiredArgsConstructor
public class InterestService {


  private final InterestRepository interestRepository;

  public List<InterestEntity> findAll() {
    return interestRepository.findAll();
  }

  public void save(InterestEntity interest){ interestRepository.save(interest); }
}