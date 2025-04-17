package pl.uj.passgo.models.DTOs.authentication.registration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;


public record OrganizerRegistrationRequest(
	@JsonProperty("credentials")
	MemberRegistrationCredentials memberRegistrationCredentials,
	String firstName,
	String lastName,
	LocalDate birthDate,
	String organization
) implements MemberRegistrationRequest {
	@Override
	public MemberRegistrationCredentials getCredentials() {
		return memberRegistrationCredentials;
	}
}
