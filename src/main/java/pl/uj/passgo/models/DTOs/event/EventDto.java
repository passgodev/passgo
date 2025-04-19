package pl.uj.passgo.models.DTOs.event;

import java.time.LocalDateTime;


public record EventDto(
	Long id,
	String name,
	LocalDateTime date,
	String description,
	String category
) {
}
