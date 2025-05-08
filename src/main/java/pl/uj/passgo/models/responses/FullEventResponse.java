package pl.uj.passgo.models.responses;
import pl.uj.passgo.models.Building;
import java.time.LocalDateTime;

public record FullEventResponse(
        Long id,
        String name,
        Building building,
        LocalDateTime date,
        String description,
        String category,
        Boolean approved
) {
}
