package ru.itmo.userservice.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.userservice.exceptions.NotFoundException;
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

//	@Autowired
//	public void setPasswordEncoder(
//		PasswordEncoder passwordEncoder) {
//		this.passwordEncoder = passwordEncoder;
//	}

//	@Autowired
//	public void setRoleService(RoleService roleService) {
//		this.roleService = roleService;
//	}

	public void deleteAll(){
		userRepository.deleteAll();
	}

//	public Optional<UserEntity> deleteByLogin(String login){
//		Optional<OwnerEntity> ownerOptional = ownerService.findByLogin(login);
//		Optional<DogEntity> dogOptional = dogService.findOptionalByLogin(login);
//		ownerOptional.ifPresent(dogService::deleteRecommendationsViaOwner);
//		dogOptional.ifPresent(dogService::deleteFromRecommendations);
//		return userRepository.deleteByLogin(login);
//	}


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
					user.setRoles(roles);
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

		return findByLogin(userDto.getLogin())
			.flatMap(existingUser -> Mono.just(new UserEntity()))
			.switchIfEmpty(createUser(userDto));
	}
	private Mono<UserEntity> createUser(UserDto userDto) {
		UserEntity user = new UserEntity();
		user.setLogin(userDto.getLogin());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));

		return Flux.fromIterable(userDto.getRoles())
			.flatMap(roleName -> roleService.findByName(roleName)
				.switchIfEmpty(Mono.error(new NotFoundException("Role not found: " + roleName))))
			.collect(Collectors.toSet())
			.flatMap(roles -> {
				user.setRoles(roles);
				return userRepository.save(user);
			});
	}

}
