package tennisboard.exception;

public class MatchIsNotFoundException extends BaseAppException {
    public MatchIsNotFoundException(String message) {
        super(message);
    }

    public MatchIsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
