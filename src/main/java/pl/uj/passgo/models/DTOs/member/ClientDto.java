package pl.uj.passgo.models.DTOs.member;

import java.time.LocalDate;
import java.time.LocalDateTime;


public record ClientDto(
	Long id,
	String firstName,
	String lastName,
	String email,
	LocalDateTime registrationDate,
	LocalDate birthDate,
	boolean isActive
) {
}
