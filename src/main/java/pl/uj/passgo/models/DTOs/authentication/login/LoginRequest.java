package pl.uj.passgo.models.DTOs.authentication.login;

public record LoginRequest(
	String login,
	String password
) {
}
