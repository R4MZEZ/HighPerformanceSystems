package ru.itmo.hotdogs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import ru.itmo.hotdogs.model.BreedEntity;
import ru.itmo.hotdogs.service.BreedService;


@RequiredArgsConstructor
//@RequestMapping(path = "/api/v1/delivery")
@RestController
public class BestRestController {

  private final BreedService breedService;

  @GetMapping("/breeds")
  public ResponseEntity<List<BreedEntity>> findAll() {
    return ResponseEntity.ok(breedService.findAll());
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Helloooo %s!", name);
  }
}