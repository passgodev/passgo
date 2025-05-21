package pl.uj.passgo.models.DTOs.event;

import java.time.LocalDateTime;

public record UpdateEventDto(
        String name,
        String description,
        String category,
        LocalDateTime date
) {}