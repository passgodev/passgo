package pl.uj.passgo.models.DTOs.authentication.registration;

import pl.uj.passgo.models.member.MemberType;


public sealed interface MemberRegistrationRequest permits ClientRegistrationRequest, OrganizerRegistrationRequest {
	MemberRegistrationCredentials getCredentials();
	MemberType getMemberType();
}
