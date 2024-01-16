package ru.itmo.ownerservice.rest;


import jakarta.ws.rs.core.MediaType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itmo.ownerservice.exceptions.NotFoundException;
import ru.itmo.ownerservice.feign.FeignConfig;
import ru.itmo.ownerservice.model.dto.ResponseDto;
import ru.itmo.ownerservice.model.entity.BreedEntity;
import ru.itmo.ownerservice.model.entity.DogEntity;

//@FeignClient(name = "dog",
//	url = "hotdogs-client/dogs",
////	url = "localhost:8081/dogs",
//	configuration = FeignConfig.class)
@FeignClient(name = "hotdogs-client",
	url = "api-gateway:8081",
	configuration = FeignConfig.class)
public interface DogsApi {
	@GetMapping(path = "/dogs/find/{id}", produces = MediaType.APPLICATION_JSON)
	ResponseDto<DogEntity> findById(@PathVariable Long id) throws NotFoundException;

	@GetMapping("/dogs/breeds/find/{name}")
	ResponseDto<BreedEntity> findBreedByName(@PathVariable String name) throws NotFoundException;
}
