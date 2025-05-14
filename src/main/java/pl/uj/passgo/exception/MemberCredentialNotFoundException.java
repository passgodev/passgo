package pl.uj.passgo.exception;

public class MemberCredentialNotFoundException extends RuntimeException {
	public MemberCredentialNotFoundException(String msg) {
		super(msg);
	}

	public MemberCredentialNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
