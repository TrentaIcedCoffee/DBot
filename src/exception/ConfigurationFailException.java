package exception;

@SuppressWarnings("serial")
public class ConfigurationFailException extends Exception {
	public ConfigurationFailException() {
		super();
	}

	public ConfigurationFailException(String message) {
		super(message);
	}
	
	public ConfigurationFailException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public ConfigurationFailException(Throwable throwable) {
		super(throwable);
	}
}
