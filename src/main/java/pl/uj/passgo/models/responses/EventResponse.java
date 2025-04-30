package pl.uj.passgo.models.responses;

import pl.uj.passgo.models.Address;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String name,
        String buidlingName,
        Address address,
        LocalDateTime date,
        String desription,
        String category,
        Boolean approved
        ) {
}
