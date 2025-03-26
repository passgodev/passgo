package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.uj.passgo.models.Building;

public interface BuildingRepository extends JpaRepository<Building, Long> {
}
