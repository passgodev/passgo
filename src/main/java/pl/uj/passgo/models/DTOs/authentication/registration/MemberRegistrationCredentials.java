package pl.uj.passgo.models.DTOs.authentication.registration;

public record MemberRegistrationCredentials(
	String login,
	String password,
	String email
) {
}
