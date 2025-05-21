package pl.uj.passgo.models.responses;
import pl.uj.passgo.models.Status;
import pl.uj.passgo.models.responses.building.BuildingResponse;

import java.time.LocalDateTime;

public record FullEventResponse(
        Long id,
        String name,
        BuildingResponse building,
        LocalDateTime date,
        String description,
        String category,
        Status status
) {
}
