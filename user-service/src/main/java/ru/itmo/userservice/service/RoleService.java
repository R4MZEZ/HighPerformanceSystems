package ru.itmo.userservice.service;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.userservice.model.entity.RoleEntity;
import ru.itmo.userservice.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
	private final RoleRepository roleRepository;

	private final Validator validator;

	public RoleEntity findByName(String name){
		return roleRepository.findByName(name).orElseThrow();
	}

	public RoleEntity createRole(@Valid RoleEntity role) throws ConstraintViolationException{
		Set<ConstraintViolation<RoleEntity>> violations = validator.validate(role);
		if (!validator.validate(role).isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
		return roleRepository.save(role);
	}

}
