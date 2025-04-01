package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Sector;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {
}
