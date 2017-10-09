package exception;

@SuppressWarnings("serial")
public class DebugRuntimeException extends RuntimeException {
	public static String message = "This exception should never happen (runtime), if you see this, please contact with opposcript@gmail.com";
	
	public DebugRuntimeException() {
		super(message);
	}
}
