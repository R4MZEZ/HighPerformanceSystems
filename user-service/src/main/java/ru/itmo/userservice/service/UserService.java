package ru.itmo.userservice.service;

import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.userservice.model.dto.UserDto;
import ru.itmo.userservice.model.entity.UserEntity;
import ru.itmo.userservice.exceptions.AlreadyExistsException;
import ru.itmo.userservice.exceptions.NotFoundException;
import ru.itmo.userservice.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private RoleService roleService;
	private final Validator validator;

	@Autowired
	public void setPasswordEncoder(
		PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Autowired
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

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


	public UserEntity findByLogin(String login) throws NotFoundException{
		return userRepository.findByLogin(login).orElseThrow(
			() -> new NotFoundException("Пользователь с таким логином не существует")
		);
	}


	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByLogin(username).orElseThrow(
			() -> new UsernameNotFoundException(
				String.format("Пользователь с логином '%s' не существует.", username)));

		return new User(user.getLogin(), user.getPassword(),
			user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
				.collect(Collectors.toList()));
	}

	public UserEntity createNewUser(@Valid UserDto userDto) throws AlreadyExistsException, ConstraintViolationException {
		Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
		if (!validator.validate(userDto).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		try{
			findByLogin(userDto.getLogin());
			throw new AlreadyExistsException("Пользователь с указанным именем уже существует");
		}catch (NotFoundException ex){
			UserEntity user = new UserEntity();
			user.setLogin(userDto.getLogin());
			user.setPassword(passwordEncoder.encode(userDto.getPassword()));
			user.setRoles(userDto.getRoles().stream().map(roleService::findByName).collect(Collectors.toSet()));
			return userRepository.save(user);
		}
	}
}
