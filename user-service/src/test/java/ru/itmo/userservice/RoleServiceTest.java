package ru.itmo.userservice;


import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.userservice.model.entity.RoleEntity;
import ru.itmo.userservice.service.RoleService;


@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
public class RoleServiceTest {

	@Autowired
	private RoleService roleService;

//	@Test
//	void validFindByNameTest(){
//		String name = "ROLE_GOD";
//		RoleEntity role = roleService.createRole(new RoleEntity(name)).block();
//		Assertions.assertDoesNotThrow(() -> roleService.findByName(name));
//		Assertions.assertEquals(name, role.getName());
//	}

	@Test
	void invalidRoleNameTest(){
		Assertions.assertThrows(ConstraintViolationException.class, () -> roleService.createRole(new RoleEntity("ROLE_GOD!!!")));
	}

}
