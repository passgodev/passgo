package pl.uj.passgo.models.responses;

import pl.uj.passgo.models.Building;
import pl.uj.passgo.models.Status;

import java.time.LocalDateTime;

public record DetailsEventResponse(
        Long id,
        String name,
        Building building,
        LocalDateTime date,
        String description,
        String category,
        Status status
) {
}
