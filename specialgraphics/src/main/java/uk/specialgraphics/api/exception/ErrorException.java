package uk.specialgraphics.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter

public class ErrorException extends RuntimeException {

    private final String variable;

    public ErrorException(String message, String variable) {
        super(message);
        this.variable = variable;
    }

}