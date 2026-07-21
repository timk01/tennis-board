package tennisboard.exception;

public abstract class BaseAppException extends RuntimeException {
    public BaseAppException(String message) {
        super(message);
    }
}
