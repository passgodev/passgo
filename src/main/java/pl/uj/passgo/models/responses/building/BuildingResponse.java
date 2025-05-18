package pl.uj.passgo.models.responses.building;

import pl.uj.passgo.models.BuildingStatus;

public record BuildingResponse(
        Long id,
        String name,
        AddressResponse address,
        BuildingStatus status
) {
}
