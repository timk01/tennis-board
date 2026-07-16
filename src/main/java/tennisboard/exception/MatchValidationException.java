package tennisboard.exception;

public class MatchValidationException extends BaseAppException {
    public MatchValidationException(String message) {
        super(message);
    }

    public MatchValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
