package pl.uj.passgo.models.responses.building;

public record RowResponse(
        Long id,
        Long rowNumber,
        Long seatsCount
) {
}
