package swyg.vitalroutes.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Body 에 담긴 Refresh Token 을 담는 DTO")
@Data
public class RefreshTokenDTO {
    private String refreshToken;
}
