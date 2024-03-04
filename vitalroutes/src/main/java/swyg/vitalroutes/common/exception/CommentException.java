package swyg.vitalroutes.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import swyg.vitalroutes.common.response.ResponseType;

@Getter
public class CommentException extends RuntimeException {
    public HttpStatus status;
    public ResponseType type;

    public CommentException(HttpStatus status, ResponseType type, String message) {
        super(message);
        this.status = status;
        this.type = type;
    }
}
