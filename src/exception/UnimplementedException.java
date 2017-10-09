package exception;

@SuppressWarnings("serial")
public class UnimplementedException extends Exception {
	public UnimplementedException() {
		super();
	}

	public UnimplementedException(String message) {
		super(message);
	}
	
	public UnimplementedException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public UnimplementedException(Throwable throwable) {
		super(throwable);
	}
}
