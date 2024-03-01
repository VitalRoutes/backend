package swyg.vitalroutes.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ApiResponseDTO<T> {
    private HttpStatus status;
    private ResponseType type;
    private String message;
    private T data;
}
