package pl.uj.passgo.models.responses.building;

public record AddressResponse(
        String country,
        String city,
        String street,
        String postalCode,
        String buildingNumber
) {
}
