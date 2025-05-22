package pl.uj.passgo.models.DTOs.weahter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record EventWeatherResponse(
	@JsonProperty("current_weather")
	CurrentWeather currentWeather,

	@JsonProperty("current_weather_units")
	WeatherUnit weatherUnits
) {
	public static record WeatherUnit(
		String windspeed,
		String temperature
	) {}

	public static record CurrentWeather(
		String windspeed,
		String temperature,
		@JsonProperty("is_day")
		Boolean isDay
	) {}
}
