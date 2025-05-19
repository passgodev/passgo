package pl.uj.passgo.models.DTOs.authentication.logout;

import java.util.UUID;


public record LogoutRequest(
	UUID refreshToken
) {
}
