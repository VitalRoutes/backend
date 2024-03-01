package swyg.vitalroutes.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Schema(description = "API 응답 스펙")
@Data
@AllArgsConstructor
public class ApiResponseDTO<T> {
    @Schema(description = "Http Status ( 숫자 X )")
    private HttpStatus status;
    @Schema(description = "임의로 정의한 type 으로 ENUM 타입으로 정의")
    private ResponseType type;
    private String message;
    @Schema(description = "서버에서 전달되는 데이터를 data 에 json 형식으로 담아서 전달")
    private T data;
}
