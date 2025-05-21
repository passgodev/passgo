package pl.uj.passgo.models.responses.building;

import pl.uj.passgo.models.Address;
import pl.uj.passgo.models.Status;

import java.util.List;

public record FullBuildingResponse(
        Long id,
        String name,
        Address address,
        Status status,
        List<SectorResponse> sectors
) {
}
