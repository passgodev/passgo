package pl.uj.passgo.models.responses;

import pl.uj.passgo.models.Address;
import pl.uj.passgo.models.Sector;

import java.util.List;

public record FullBuildingResponse(
        Long id,
        String name,
        Address address,
        Boolean approved,
        List<Sector> sectors
) {
}
