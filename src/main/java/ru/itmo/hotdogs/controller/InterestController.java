package ru.itmo.hotdogs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import ru.itmo.hotdogs.model.entity.BreedEntity;
import ru.itmo.hotdogs.model.entity.InterestEntity;
import ru.itmo.hotdogs.service.BreedService;
import ru.itmo.hotdogs.service.InterestService;


@RequiredArgsConstructor
@RequestMapping(path = "/interests")
@RestController
public class InterestController {

  private final InterestService interestService;

  @GetMapping
  public ResponseEntity<List<InterestEntity>> findAll() {
    return ResponseEntity.ok(interestService.findAll());
  }

  @PostMapping("/new")
  public ResponseEntity<?> addInterest(@RequestBody InterestEntity interest) {
    interestService.save(interest);
    return ResponseEntity.ok("Интерес успешно создан");
  }
}