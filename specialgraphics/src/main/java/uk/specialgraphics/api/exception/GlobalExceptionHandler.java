package uk.specialgraphics.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(ErrorException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getVariable());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    static class ErrorResponse {
        private final String message;
        private final String variable;

        public ErrorResponse(String message, String code) {
            this.message = message;
            this.variable = code;
        }

        public String getMessage() {
            return message;
        }

        public String getCode() {
            return variable;
        }
    }
}
