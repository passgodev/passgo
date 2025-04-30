package pl.uj.passgo.models.responses;
import pl.uj.passgo.models.Building;
import java.time.LocalDateTime;

public record FullEventResponse(
        Long id,
        String name,
        Building building,
        LocalDateTime date,
        String desription,
        String category,
        Boolean approved
) {
}
