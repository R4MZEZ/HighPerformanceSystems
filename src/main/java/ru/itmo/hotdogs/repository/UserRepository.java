package ru.itmo.hotdogs.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.dto.RecommendedUserDto;
import ru.itmo.hotdogs.model.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  @Query(value="select * from (SELECT users.id as id, users.name as username, age, ST_Distance(location, (ST_MakePoint(:longitude, :latitude))) as distance "
      + "from users inner join owners on users.owner = owners.id order by distance) as uad left join users_interactions on "
      + "uad.id = receiver_id where distance > 0 and users_interactions.id is null", nativeQuery = true)
  List<RecommendedUserDto> findNearest(Double longitude, Double latitude);
  @Query(value="SELECT id, name as username, age, ST_Distance(ST_MakePoint(:longitude1, :latitude1), ST_MakePoint(:longitude2, :latitude2), true) from users where id=:id", nativeQuery = true)
  RecommendedUserDto findDistance(Long id, Double longitude1, Double latitude1, Double longitude2, Double latitude2);
}