package ru.itmo.hotdogs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.hotdogs.model.entity.RoleEntity;
import ru.itmo.hotdogs.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
	private final RoleRepository roleRepository;

	public RoleEntity findByName(String name) {
		return roleRepository.findByName(name).get();
	}

}
