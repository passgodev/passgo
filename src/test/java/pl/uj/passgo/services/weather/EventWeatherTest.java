package pl.uj.passgo.services.weather;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pl.uj.passgo.exception.weather.EventWeatherException;
import pl.uj.passgo.models.DTOs.weahter.EventWeatherRequest;
import pl.uj.passgo.models.DTOs.weahter.EventWeatherResponse;

import java.net.URI;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventWeatherTest {
	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private EventWeatherService eventWeatherTest;


	@Test
	public void testGetWeather_success() throws EventWeatherException {
		// arrange
		when(restTemplate.getForEntity(any(URI.class), eq(EventWeatherService.NominationWeahterResponse[].class)))
			.thenReturn(new ResponseEntity<>(getNominationWeahterResponse(), HttpStatus.OK));

		when(restTemplate.getForEntity(any(URI.class), eq(EventWeatherResponse.class)))
			.thenReturn(new ResponseEntity<>(getWeatherResponse(), HttpStatus.OK));

		// act
		var weatherResult = eventWeatherTest.getWeather(getValidWeatherRequest());

		// assert
		Assertions.assertNotNull(weatherResult);
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@NullSource
	public void testGetWeather_NoCountry(String country) {
		// arrange
		var request = new EventWeatherRequest(
			country,
			"Berlin",
			Optional.empty()
		);

		// act
		Executable shouldFail = () -> eventWeatherTest.getWeather(request);

		// assert
		Assertions.assertThrows(EventWeatherException.class, shouldFail);
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@NullSource
	public void testGetWeather_NoCitys(String city) {
		// arrange
		var request = new EventWeatherRequest(
			"Poland",
			city,
			Optional.empty()
		);

		// act
		Executable shouldFail = () -> eventWeatherTest.getWeather(request);

		// assert
		Assertions.assertThrows(EventWeatherException.class, shouldFail);
	}

	@Test
	public void testGetWeather_ExternalServiceFailed() {
		// arrange
		var restClientException = RestClientException.class;
		when(restTemplate.getForEntity(any(URI.class), any()))
			.thenThrow(restClientException);
		var validRequest = getValidWeatherRequest();

		// act
		Executable shouldFail = () -> eventWeatherTest.getWeather(validRequest);

		// assert
		Assertions.assertThrows(EventWeatherException.class, shouldFail);
	}

	private static EventWeatherRequest getValidWeatherRequest() {
		return new EventWeatherRequest(
			"Warsaw",
			"Poland",
			Optional.empty()
		);
	}

	private static EventWeatherService.NominationWeahterResponse[] getNominationWeahterResponse() {
		return new EventWeatherService.NominationWeahterResponse[] {
			new EventWeatherService.NominationWeahterResponse(
				"10.15",
				"21.15"
			)
		};
	}

	private static EventWeatherResponse getWeatherResponse() {
		return new EventWeatherResponse(
			new EventWeatherResponse.CurrentWeather(
				"10.00",
				"10.00",
				true
			),
			new EventWeatherResponse.WeatherUnit(
				"km/h",
				"Celsius"
			)
		);
	}

}
