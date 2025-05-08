package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.uj.passgo.models.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}

