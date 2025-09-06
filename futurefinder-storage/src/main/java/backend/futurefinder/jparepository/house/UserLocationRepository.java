package backend.futurefinder.jparepository.house;

import backend.futurefinder.jpaentity.user.UserLocationJpaEntity;
import backend.futurefinder.model.user.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<UserLocationJpaEntity, Long> {

    List<UserLocationJpaEntity> findByUserIdAndLocationType(String userId, LocationType locationType);

    Optional<UserLocationJpaEntity> findFirstByUserIdAndLocationType(String userId, LocationType locationType);

    void deleteByUserIdAndLocationType(String userId, LocationType locationType);

    boolean existsByUserIdAndLocationTypeAndProvinceAndCity(
            String userId, LocationType locationType, String province, String city);
}