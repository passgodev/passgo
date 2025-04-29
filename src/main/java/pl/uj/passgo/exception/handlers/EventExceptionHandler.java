package pl.uj.passgo.exception.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;


@Slf4j
@RestControllerAdvice
public class EventExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionMessageResponse> handleResponseStatusException(ResponseStatusException ex) {
        var body = new ExceptionMessageResponse(ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ExceptionMessageResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        var body = new ExceptionMessageResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessageResponse> handleException(Exception ex) {
        log.debug("Unhandled exception stack trace.\nMessage: {}\nStackTrace: {}", ex.getMessage(), Arrays.toString(ex.getStackTrace()));
        log.error("Unhandled exception captured, returning status code 500");
        var response = new ExceptionMessageResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
