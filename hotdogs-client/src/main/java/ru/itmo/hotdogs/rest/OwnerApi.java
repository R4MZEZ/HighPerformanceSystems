package ru.itmo.hotdogs.rest;

import jakarta.ws.rs.core.MediaType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.itmo.hotdogs.exceptions.NotFoundException;
import ru.itmo.hotdogs.exceptions.ServiceUnavalibleException;
import ru.itmo.hotdogs.feign.FeignConfig;
import ru.itmo.hotdogs.model.dto.ResponseDto;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.OwnerEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.rest.OwnerApi.OwnerApiFallback;

@FeignClient(
	name = "owner-service",
	url = "localhost:8081",
	configuration = FeignConfig.class
	,fallback = OwnerApiFallback.class
)
//@FeignClient(
//	name = "owner-service",
//	configuration = FeignConfig.class
//	,fallback = OwnerApiFallback.class
//)
public interface OwnerApi {

	@GetMapping(path = "/owners/find/{login}", produces = MediaType.APPLICATION_JSON)
//	@Bulkhead(name = "x", fallbackMethod = "findByLogin")
	OwnerEntity findByLogin(@PathVariable String login)
		throws NotFoundException, ServiceUnavalibleException;

	@PostMapping(path = "/owners/shows/{showId}/addParticipant", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
//	@Bulkhead(name = "y", fallbackMethod = "addParticipant")
	ResponseDto<?> addParticipant(@PathVariable Long showId, @RequestBody DogEntity dog) throws ServiceUnavalibleException;

	@Component
	class OwnerApiFallback implements OwnerApi {

		@Override
		public OwnerEntity findByLogin(String login) throws ServiceUnavalibleException {
			throw new ServiceUnavalibleException("Сервис  временно недоступен, попробуйте позже");
		}

		@Override
		public ResponseDto<ShowEntity> addParticipant(Long showId, DogEntity dog)
			throws ServiceUnavalibleException {
			throw new ServiceUnavalibleException("Организатор выставок вышел покурить, попробуйте позже");
		}
	}
}
