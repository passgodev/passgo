package pl.uj.passgo.models.DTOs.authentication.refresh;

import java.util.UUID;


public record RefreshTokenRequest(
	UUID refreshToken
) {}
