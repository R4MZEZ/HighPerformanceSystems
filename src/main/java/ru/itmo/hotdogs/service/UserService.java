package ru.itmo.hotdogs.service;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByLogin(username).orElseThrow(
			() -> new UsernameNotFoundException(
				String.format("Пользователь с логином '%s' не существует.", username)));

		return new User(user.getLogin(), user.getPassword(),
			user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
				.collect(Collectors.toList()));
	}
}
