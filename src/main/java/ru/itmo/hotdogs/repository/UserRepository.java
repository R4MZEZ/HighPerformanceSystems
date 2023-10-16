package ru.itmo.hotdogs.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.dto.RecommendedUserDto;
import ru.itmo.hotdogs.model.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	@Query(value =
		"select * from (select users.id, users.name, age, ST_Distance(location, (ST_MakePoint(:longitude, :latitude))) as distance "
			+ "from users inner join owners on users.owner=owners.id where users.id not in "
			+ "(select receiver_id from users_interactions where sender_id = :id) order by distance) as query "
			+ "where distance > 0 limit 1", nativeQuery = true)
	RecommendedUserDto findNearest(Double longitude, Double latitude, Long id);

	@Query(value = "SELECT id, name, age, ST_Distance(ST_MakePoint(:longitude1, :latitude1), ST_MakePoint(:longitude2, :latitude2), true) as distance from users where id=:id", nativeQuery = true)
	RecommendedUserDto findDistance(Long id, Double longitude1, Double latitude1, Double longitude2,
		Double latitude2);
}
