package tennisboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tennisboard.response.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Map<Class<? extends BaseAppException>, HttpStatus> HTTP_STATUS_MAP = new HashMap<>();

    static {
        HTTP_STATUS_MAP.put(MatchValidationException.class, HttpStatus.BAD_REQUEST);
        HTTP_STATUS_MAP.put(MatchIsNotFoundException.class, HttpStatus.NOT_FOUND);
        HTTP_STATUS_MAP.put(MatchAlreadyFinishedException.class, HttpStatus.NOT_FOUND);
        HTTP_STATUS_MAP.put(SideIsNotFoundException.class, HttpStatus.BAD_REQUEST);
        HTTP_STATUS_MAP.put(PlayerNameAlreadyExistsException.class, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BaseAppException.class)
    public ResponseEntity<ErrorResponse> handleKnownException(BaseAppException exception) {
        HttpStatus status = HTTP_STATUS_MAP.getOrDefault(exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponse response = new ErrorResponse(
                exception.getMessage()
        );
        //toDo logger for ex ?
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleSpringMVCException() {
        return new ResponseEntity<>(
                new ErrorResponse("Invalid request parameter"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException() {
        return new ResponseEntity<>(
                new ErrorResponse("Unknown exception"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
