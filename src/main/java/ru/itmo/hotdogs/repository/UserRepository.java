package ru.itmo.hotdogs.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.itmo.hotdogs.model.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  @Query(value="SELECT users.id,users.name,age,breed,owner from users inner join owners on users.owner = owners.id "
      + "order by ST_Distance(location, ST_MakePoint(:longitude, :latitude)::geography)", nativeQuery = true)
  List<UserEntity> findNearest(Double longitude, Double latitude);
}