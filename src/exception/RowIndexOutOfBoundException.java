package exception;

@SuppressWarnings("serial")
public class RowIndexOutOfBoundException extends RuntimeException {
	public RowIndexOutOfBoundException() {
		super();
	}

	public RowIndexOutOfBoundException(String message) {
		super(message);
	}
	
	public RowIndexOutOfBoundException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public RowIndexOutOfBoundException(Throwable throwable) {
		super(throwable);
	}
}

