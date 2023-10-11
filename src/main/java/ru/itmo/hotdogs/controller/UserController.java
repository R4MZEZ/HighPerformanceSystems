package ru.itmo.hotdogs.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.model.dto.RecommendedUserDto;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.service.UserService;

@RequiredArgsConstructor
@RequestMapping(path = "/users")
@RestController
public class UserController {

  private final UserService userService;

  @PostMapping(path = "/new")
  public ResponseEntity<?> createUser(@RequestBody RecommendedUserDto user) {
    userService.save(user);
    return ResponseEntity.ok("Пользователь успешно создан");
  }

  @GetMapping("/{id}/recommend")
  public ResponseEntity<List<RecommendedUserDto>> getUsersNear(@PathVariable long id) {
    try {
      return ResponseEntity.ok(userService.findAround(id));
    }catch (NotFoundException e){
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping()
  public ResponseEntity<List<UserEntity>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }
}
