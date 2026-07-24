package tennisboard.exception;

public class PlayerNameAlreadyExistsException extends BaseAppException {

    public PlayerNameAlreadyExistsException(String message) {
        super(message);
    }

    public PlayerNameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
