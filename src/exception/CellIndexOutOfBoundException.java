package exception;

@SuppressWarnings("serial")
public class CellIndexOutOfBoundException extends RuntimeException {
	public CellIndexOutOfBoundException() {
		super();
	}

	public CellIndexOutOfBoundException(String message) {
		super(message);
	}
	
	public CellIndexOutOfBoundException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public CellIndexOutOfBoundException(Throwable throwable) {
		super(throwable);
	}
}
