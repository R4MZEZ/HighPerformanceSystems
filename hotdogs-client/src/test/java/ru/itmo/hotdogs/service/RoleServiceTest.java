package ru.itmo.hotdogs.service;


import java.util.NoSuchElementException;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hotdogs.model.entity.RoleEntity;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
public class RoleServiceTest {

	@Autowired
	private RoleService roleService;

	@Test
	void validFindByNameTest(){
		String name = "ROLE_GOD";
		RoleEntity role = roleService.createRole(new RoleEntity(name));
		Assertions.assertDoesNotThrow(() -> roleService.findByName(name));
		Assertions.assertEquals(name, role.getName());
	}

	@Test
	void invalidRoleNameTest(){
		Assertions.assertThrows(ConstraintViolationException.class, () -> roleService.createRole(new RoleEntity("ROLE_GOD!!!")));
	}

	@Test
	void roleNotFoundNameTest(){
		Assertions.assertThrows(NoSuchElementException.class, () -> roleService.findByName("ROLE_CAT"));
	}

}
