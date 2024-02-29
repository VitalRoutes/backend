package swyg.vitalroutes.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JwtTokenException extends RuntimeException {

    public int statusCode;

    public JwtTokenException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
