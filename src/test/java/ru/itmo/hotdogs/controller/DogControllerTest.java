package ru.itmo.hotdogs.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.model.dto.JwtResponse;
import ru.itmo.hotdogs.model.entity.UserEntity;
import ru.itmo.hotdogs.repository.UserRepository;
import ru.itmo.hotdogs.service.BreedService;
import ru.itmo.hotdogs.service.DogService;
import ru.itmo.hotdogs.service.InterestService;
import ru.itmo.hotdogs.service.OwnerService;
import ru.itmo.hotdogs.service.RoleService;
import ru.itmo.hotdogs.service.UserService;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class DogControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	static private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private RoleService roleService;
	@Autowired
	private BreedService breedService;
	@Autowired
	private InterestService interestService;
	@Autowired
	private DogService dogService;
	@Autowired
	private OwnerService ownerService;
	@Autowired
	private UserService userService;


	@BeforeAll
	void addAdmin() {
		try {
			userService.findByLogin("admin");
		} catch (NotFoundException e) {
			userRepository.save(new UserEntity("admin", new BCryptPasswordEncoder().encode("admin"),
				Set.of(roleService.findByName("ROLE_ADMIN"))));
		}
	}

	@AfterAll
	void clearData() {
		dogService.deleteAll();
		breedService.deleteAll();
		interestService.deleteAll();
		ownerService.deleteAll();
		userService.deleteAll();
	}


	@Test
	void a_createDogTest() throws Exception {
		String jsonBody = "{\"login\": \"admin\", \"password\": \"admin\"}";
		MvcResult result = this.mockMvc.perform(
				post("/auth")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();
		JwtResponse jwtResponse = objectMapper.readValue(response.getContentAsString(),
			JwtResponse.class);

		jsonBody = """
			{
				"userInfo":{
					"login": "katya111",
					"password": "passsword"
				},
				"ownerInfo":{
					"name": "Katya",
					"balance": 100,
					"latitude": 9,
					"longitude": 8,
					"isOrganizer": false
			    }
			}""";
		result = this.mockMvc.perform(
				post("/owners/new")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andReturn();

		jsonBody = """
			{
			    "name": "taksa"
			}""";
		result = this.mockMvc.perform(
				post("/breeds/new")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andReturn();

		jsonBody = """
			{
				"userInfo":{
					"login": "logggin",
					"password": "passsword"
				},
				"dogInfo":{
					"name": "hardik",
					"age": 4,
					"breed": "taksa",
					"ownerLogin": "katya111",
					"interests": {}
				}
			}""";
		this.mockMvc.perform(
				post("/dogs/new")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andExpect(status().isCreated());
	}

	@Test
	void b_addInterestTest() throws Exception {
		String jsonBody = "{\"login\": \"admin\", \"password\": \"admin\"}";
		MvcResult result = this.mockMvc.perform(
				post("/auth")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();
		JwtResponse jwtResponse = objectMapper.readValue(response.getContentAsString(),
			JwtResponse.class);

		jsonBody = """
			{
			    "name": "hunting"
			}""";
		result = this.mockMvc.perform(
				post("/interests/new")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andReturn();

		jsonBody = """
			{
			    "name": "walking"
			}""";
		result = this.mockMvc.perform(
				post("/interests/new")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andReturn();

		jsonBody = """
			{
			    "login": "logggin",
			    "password": "passsword"
			}""";
		result = this.mockMvc.perform(
				post("/auth")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON))
			.andReturn();

		jwtResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
			JwtResponse.class);

		jsonBody = """
			{
			    "interestName": "hunting",
			    "level": 7
			}""";
		this.mockMvc.perform(
				patch("/dogs/add-interest")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andExpect(status().isOk());

		this.mockMvc.perform(
				patch("/dogs/add-interest")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andExpect(status().isBadRequest());

		jsonBody = """
			{
			    "interestName": "fishing",
			    "level": 3
			}""";
		this.mockMvc.perform(
				patch("/dogs/add-interest")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andExpect(status().isNotFound());

		jsonBody = """
			{
			    "interestName": "walking",
			    "level": -1
			}""";
		this.mockMvc.perform(
				patch("/dogs/add-interest")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andExpect(status().isBadRequest());

	}


	@Test
	void c_interactionTest() throws Exception {
		String jsonBody = "{\"login\": \"admin\", \"password\": \"admin\"}";
		MvcResult result = this.mockMvc.perform(
				post("/auth")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();
		JwtResponse jwtResponse = objectMapper.readValue(response.getContentAsString(),
			JwtResponse.class);

		jsonBody = """
			{
				"userInfo":{
					"login": "toha222",
					"password": "password"
				},
				"ownerInfo":{
					"name": "Anton",
					"balance": 1000,
					"latitude": 2,
					"longitude": 2,
					"isOrganizer": true
				}
			}""";
		result = this.mockMvc.perform(
				post("/owners/new")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andReturn();

		jsonBody = """
			{
				"userInfo":{
					"login": "snoop_dog",
					"password": "password"
				},
				"dogInfo":{
					"name": "Snoopy",
					"age": 2,
					"breed": "taksa",
					"ownerLogin": "toha222",
					"interests": {
						"hunting": 3,
						"walking": 8
					}
				}
			}""";
		this.mockMvc.perform(
				post("/dogs/new")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andExpect(status().isCreated());

		jsonBody = """
			{
			    "login": "snoop_dog",
			    "password": "password"
			}""";
		result = this.mockMvc.perform(
				post("/auth")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON))
			.andReturn();

		jwtResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
			JwtResponse.class);

		this.mockMvc.perform(
				get("/dogs/recommend")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andExpect(status().isOk());

		this.mockMvc.perform(
				post("/dogs/rate")
					.contentType(MediaType.APPLICATION_JSON)
					.param("is_like", "true")
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andExpect(status().isOk());
	}

	@Test
	void d_matchTest() throws Exception {
		String jsonBody = """
			{
			    "login": "logggin",
			    "password": "passsword"
			}""";
		MvcResult result = this.mockMvc.perform(
				post("/auth")
					.contentType(MediaType.APPLICATION_JSON)
					.content(jsonBody)
					.accept(MediaType.APPLICATION_JSON))
			.andReturn();

		JwtResponse jwtResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
			JwtResponse.class);

		this.mockMvc.perform(
				get("/dogs/recommend")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andExpect(status().isOk());

		result = this.mockMvc.perform(
				post("/dogs/rate")
					.contentType(MediaType.APPLICATION_JSON)
					.param("is_like", "true")
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andReturn();

		Assertions.assertTrue(
			result.getResponse().getContentAsString().startsWith("It's a match!"));

		result = this.mockMvc.perform(
				get("/dogs/me")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + jwtResponse.getToken()))
			.andExpect(status().isOk()).andReturn();
	}

}
