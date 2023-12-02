package ru.itmo.ownerservice.rest;


import jakarta.ws.rs.core.MediaType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itmo.ownerservice.exceptions.NotFoundException;
import ru.itmo.ownerservice.feign.FeignConfig;
import ru.itmo.ownerservice.model.entity.BreedEntity;
import ru.itmo.ownerservice.model.entity.DogEntity;

@FeignClient(name = "dog", url = "localhost:8081/dogs", configuration = FeignConfig.class)
public interface DogsApi {
	@GetMapping(path = "/find/{id}", produces = MediaType.APPLICATION_JSON)
	DogEntity findById(@PathVariable Long id) throws NotFoundException;

	@GetMapping(path = "breeds/find/{name}", produces = MediaType.APPLICATION_JSON)
	BreedEntity findBreedByName(@RequestParam String name) throws NotFoundException;
}
