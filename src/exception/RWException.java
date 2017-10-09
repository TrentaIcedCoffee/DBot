package exception;

@SuppressWarnings("serial")
public class RWException extends Exception {
	public RWException() {
		super();
	}

	public RWException(String message) {
		super(message);
	}
	
	public RWException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public RWException(Throwable throwable) {
		super(throwable);
	}
}