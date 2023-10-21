package ru.itmo.hotdogs.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.dto.RecommendedDogDto;
import ru.itmo.hotdogs.model.entity.DogEntity;
import ru.itmo.hotdogs.model.entity.ShowEntity;
import ru.itmo.hotdogs.model.entity.UserEntity;

@Repository
public interface DogRepository extends JpaRepository<DogEntity, Long> {

	@Query(value =
		"select * from (select dogs.id, dogs.name, age, ST_Distance(location, (ST_MakePoint(:longitude, :latitude))) as distance "
			+ "from dogs inner join owners on dogs.owner=owners.id where dogs.id not in "
			+ "(select receiver_id from dogs_interactions where sender_id = :id) order by distance) as query "
			+ "where distance > 0 limit 1", nativeQuery = true)
	RecommendedDogDto findNearest(Double longitude, Double latitude, Long id);

	@Query(value = "SELECT id, name, age, ST_Distance(ST_MakePoint(:longitude1, :latitude1), ST_MakePoint(:longitude2, :latitude2), true) as distance from dogs where id=:id", nativeQuery = true)
	RecommendedDogDto findDistance(Long id, Double longitude1, Double latitude1, Double longitude2, Double latitude2);

	Optional<DogEntity> findByUser(UserEntity userEntity);
}
