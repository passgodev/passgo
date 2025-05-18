package pl.uj.passgo.models.responses.building;

import pl.uj.passgo.models.Address;
import pl.uj.passgo.models.BuildingStatus;

import java.util.List;

public record FullBuildingResponse(
        Long id,
        String name,
        Address address,
        BuildingStatus status,
        List<SectorResponse> sectors
) {
}
