package pl.uj.passgo.models.DTOs.authentication.login;

import java.util.UUID;


public record LoginResponse(
	UUID refreshToken,
	String token
) {
}
