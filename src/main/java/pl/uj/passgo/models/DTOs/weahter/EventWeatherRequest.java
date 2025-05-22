package pl.uj.passgo.models.DTOs.weahter;

import java.util.Optional;


public record EventWeatherRequest(
	String countryName,
	String cityName,
	Optional<String> postalCode
) {}
