package pl.uj.passgo.exception.weather;


public class EventWeatherException extends Exception {
	public static enum FaultReason {
		SERVICE_UNAVAILABLE,
		INVALID_RESPONSE,
		INVALID_REQUEST,
	}

	private final FaultReason faultReason;

	public EventWeatherException(FaultReason faultReason, String message) {
		super(message);
		this.faultReason = faultReason;
	}

	public EventWeatherException(FaultReason faultReason, String message, Throwable cause) {
		super(message, cause);
		this.faultReason = faultReason;
	}

	public FaultReason getFaultSide() {
		return faultReason;
	}
}
