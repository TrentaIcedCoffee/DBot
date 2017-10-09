package exception;

@SuppressWarnings("serial")
public class DebugException extends Exception {
	public static String message = "This exception should never happen, if you see this, please contact with opposcript@gmail.com";
	
	public DebugException() {
		super(message);
	}
}
