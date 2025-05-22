package pl.uj.passgo.services.weather;

public interface WeatherService<T, R, E extends Exception> {
	T getWeather(R request) throws E;
}
