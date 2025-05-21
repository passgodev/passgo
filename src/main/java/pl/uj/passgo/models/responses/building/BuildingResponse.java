package pl.uj.passgo.models.responses.building;

import pl.uj.passgo.models.Status;

public record BuildingResponse(
        Long id,
        String name,
        AddressResponse address,
        Status status
) {
}
