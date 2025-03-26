package exception;

public class ManagerReadFileException extends RuntimeException {
    public ManagerReadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
