package pl.uj.passgo.models.responses.building;

import java.util.List;

public record SectorResponse(
        String name,
        Boolean standingArea,
        List<RowResponse> rows
) {
}
