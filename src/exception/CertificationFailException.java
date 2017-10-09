package exception;

@SuppressWarnings("serial")
public class CertificationFailException extends Exception {
	public CertificationFailException() {
		super();
	}

	public CertificationFailException(String message) {
		super(message);
	}
	
	public CertificationFailException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public CertificationFailException(Throwable throwable) {
		super(throwable);
	}
}