package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Building;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
}
