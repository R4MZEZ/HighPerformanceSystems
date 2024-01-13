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

//@FeignClient(name = "breed",
//	url = "hotdogs-client/dogs/breeds",
////	url = "localhost:8081/dogs/breeds",
//	configuration = FeignConfig.class)
@FeignClient(name = "hotdogs-client",
	configuration = FeignConfig.class)
public interface BreedsApi {
	@GetMapping("/dogs/breeds/find/{name}")
	ResponseDto<BreedEntity> findBreedByName(@PathVariable String name) throws NotFoundException;
}
