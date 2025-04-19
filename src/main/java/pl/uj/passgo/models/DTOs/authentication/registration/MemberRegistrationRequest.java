package pl.uj.passgo.models.DTOs.authentication.registration;

public sealed interface MemberRegistrationRequest permits ClientRegistrationRequest, OrganizerRegistrationRequest {
	MemberRegistrationCredentials getCredentials();
}
