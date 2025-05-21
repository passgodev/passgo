package pl.uj.passgo.models.responses;

import pl.uj.passgo.models.Status;
import pl.uj.passgo.models.responses.building.AddressResponse;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String name,
        String buildingName,
        AddressResponse address,
        LocalDateTime date,
        String description,
        String category,
        Status status
        ) {
}
