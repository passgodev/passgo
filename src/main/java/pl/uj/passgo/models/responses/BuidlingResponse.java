package pl.uj.passgo.models.responses;

import pl.uj.passgo.models.Address;

public record BuidlingResponse(
        Long id,
        String name,
        Address address,
        Boolean approved
) {
}
