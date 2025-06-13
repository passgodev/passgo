package pl.uj.passgo.services.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.uj.passgo.exception.weather.EventWeatherException;
import pl.uj.passgo.models.DTOs.weahter.EventWeatherRequest;
import pl.uj.passgo.models.DTOs.weahter.EventWeatherResponse;

import java.net.URI;
import java.util.Arrays;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventWeatherService implements WeatherService<EventWeatherRequest, EventWeatherResponse, EventWeatherException> {
	private static final String LOCATION_NAME_TO_COORDINATE_SERVICE_URL = "https://nominatim.openstreetmap.org/search";
	private static final String WEATHER_SERVICE_URL = "https://api.open-meteo.com/v1/forecast";

	private final RestTemplate restTemplate;

	@Override
	public EventWeatherResponse getWeather(EventWeatherRequest request) throws EventWeatherException {
		log.info("getWeather - invoked");

		log.info("Getting coordinates for {}", request);
		var locationCoordinates = convertNamedLocationToCoordinates(request);
		log.info("Location coordinate {}", locationCoordinates);

		log.info("Getting weather for {}", locationCoordinates);
		var locationWeather = convertCoordinatesToWeather(locationCoordinates);
		log.info("Location weather {}", locationWeather);

		return locationWeather;
	}

	private NominationWeahterResponse convertNamedLocationToCoordinates(EventWeatherRequest request) throws EventWeatherException {
		var uriBuilder = UriComponentsBuilder
			.fromUriString(LOCATION_NAME_TO_COORDINATE_SERVICE_URL);

		handleAppendQueryParamToUri(uriBuilder, "country", request.countryName());
		handleAppendQueryParamToUri(uriBuilder, "city", request.cityName());

		if ( request.postalCode() != null ) {
			request.postalCode()
				.filter(postalCode -> !postalCode.isBlank())
		   		.ifPresent(postalCode -> uriBuilder.queryParam("postalcode", postalCode));
		}

		uriBuilder.queryParam("format", "json");
		uriBuilder.queryParam("limit", "1");

		var uri = uriBuilder.build().toUri();
		var response = callExternalRestServiceGetMethod(uri, NominationWeahterResponse[].class);

		// We are interested only in first returned record, foreign api, does not provide cleaner way to retrieve first element
		if (response.getBody() != null && response.getBody().length > 0) {
			var firstRecord = response.getBody()[0];
			return firstRecord;
		} else {
			log.error("Coordinate service invalid body context: {}", Arrays.toString(response.getBody()));
			throw new EventWeatherException(EventWeatherException.FaultReason.INVALID_RESPONSE, "Coordinate service invalid response body");
		}
	}

	private void handleAppendQueryParamToUri(UriComponentsBuilder uriBuilder, String queryParamName, String queryParamValue) throws EventWeatherException {
		if ( queryParamValue != null && !queryParamValue.isBlank() ) {
			uriBuilder.queryParam(queryParamName, queryParamValue);
		} else {
			log.error(queryParamName + " query parameter value is blank or null, aborting request");
			throw new EventWeatherException(EventWeatherException.FaultReason.INVALID_REQUEST, queryParamName + " query parameter can not be blank or absent");
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static record NominationWeahterResponse(
		@JsonProperty("lat")
		String lat,
		@JsonProperty("lon")
		String lon
	) {}

	private EventWeatherResponse convertCoordinatesToWeather(NominationWeahterResponse request) throws EventWeatherException {
		var uriBuilder = UriComponentsBuilder
			.fromUriString(WEATHER_SERVICE_URL)
			.queryParam("latitude", request.lat())
			.queryParam("longitude", request.lon())
			.queryParam("current_weather", true);

		var uri = uriBuilder.build().toUri();
		var response = callExternalRestServiceGetMethod(uri, EventWeatherResponse.class);

		return response.getBody();
	}

	private <T> ResponseEntity<T> callExternalRestServiceGetMethod(URI uri, Class<T> responseType) throws EventWeatherException {
		try {
			log.info("Sending uri to foreign service, uri: {}", uri);
			return restTemplate.getForEntity(uri, responseType);
		} catch ( RestClientException e ) {
			log.error("Call to external service (uri: {}) failed, exception: {}", uri, e.getMessage());
			throw new EventWeatherException(EventWeatherException.FaultReason.SERVICE_UNAVAILABLE, uri.getUserInfo() + " service internal error", e);
		}
	}
}
