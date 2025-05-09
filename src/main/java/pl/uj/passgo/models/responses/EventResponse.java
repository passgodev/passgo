package pl.uj.passgo.models.responses;

import pl.uj.passgo.models.Address;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String name,
        String buildingName,
        Address address,
        LocalDateTime date,
        String description,
        String category,
        Boolean approved
        ) {
}
