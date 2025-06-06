package pl.uj.passgo.models.DTOs.authentication.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.uj.passgo.models.member.MemberType;

import java.time.LocalDate;


public record ClientRegistrationRequest(
	@JsonProperty("credentials")
	MemberRegistrationCredentials memberRegistrationCredentials,
	String firstName,
	String lastName,
	LocalDate birthDate
) implements MemberRegistrationRequest {
	@Override
	public MemberRegistrationCredentials getCredentials() {
		return memberRegistrationCredentials;
	}

	@Override
	public MemberType getMemberType() {
		return MemberType.CLIENT;
	}
}
