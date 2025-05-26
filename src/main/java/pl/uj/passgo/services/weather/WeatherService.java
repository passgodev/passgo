package pl.uj.passgo.services.weather;

public interface WeatherService<ReqT, ResT, E extends Exception> {
	ResT getWeather(ReqT request) throws E;
}
