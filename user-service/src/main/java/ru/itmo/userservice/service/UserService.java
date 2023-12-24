package ru.itmo.userservice.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.itmo.userservice.exceptions.AlreadyExistsException;
import ru.itmo.userservice.model.dto.UserDto;
import ru.itmo.userservice.model.entity.UserEntity;
import ru.itmo.userservice.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleService roleService;
	private final Validator validator;

	public Mono<UserEntity> findByLogin(String login) {
		return userRepository.findByLogin(login);
	}


	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return userRepository.findByLogin(username)
			.switchIfEmpty(Mono.error(new UsernameNotFoundException("User Not Found")))
			.flatMap(user -> roleService.findUserRolesByLogin(user.getLogin())
				.collect(Collectors.toSet())
				.map(roles -> {
					List<SimpleGrantedAuthority> authorities = roles.stream()
						.map(role -> new SimpleGrantedAuthority(role.getName()))
						.collect(Collectors.toList());
					return new User(user.getLogin(), user.getPassword(), authorities);
				}));
	}


	public Mono<UserEntity> createNewUser(@Valid UserDto userDto) {
		Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
		if (!violations.isEmpty()) {
			return Mono.error(new ConstraintViolationException(violations));
		}

		return createUser(userDto).onErrorReturn(new UserEntity());

	}

	private Mono<UserEntity> createUser(UserDto userDto) {
		UserEntity user = new UserEntity();
		user.setLogin(userDto.getLogin());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));

		return userRepository.save(user);
	}

	public Mono<UserEntity> addRole(Long userId, Integer roleId) {
		return userRepository.addRole(userId, roleId);
	}
}