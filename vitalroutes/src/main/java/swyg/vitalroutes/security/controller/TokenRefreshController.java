package swyg.vitalroutes.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import swyg.vitalroutes.common.exception.JwtTokenException;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.security.domain.RefreshTokenDTO;
import swyg.vitalroutes.security.utils.JwtConstants;
import swyg.vitalroutes.security.utils.JwtTokenProvider;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;

@Tag(name = "Token Refresh Controller", description = "토큰 갱신을 위한 controller ")
@Slf4j
@RestController
@RequiredArgsConstructor
public class TokenRefreshController {

    private final JwtTokenProvider jwtTokenProvider;

    @Operation(description = "{'refreshToken' : 'aaaaaa'} 형태로 전달필요", summary = "Access Token 갱신")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "정상적으로 access token 이 갱신됨",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/token/refresh")
    public ApiResponseDTO<?> tokenRefresh(@RequestHeader("Authorization") String authHeader, @RequestBody RefreshTokenDTO dto) {
        jwtTokenProvider.checkAuthorizationHeader(authHeader);
        String accessToken = jwtTokenProvider.getTokenFromHeader(authHeader);
        String refreshToken = dto.getRefreshToken();

        log.info("refresh Token = {}", refreshToken);

        if (refreshToken == null) {
            return new ApiResponseDTO<>(BAD_REQUEST, FAIL, "refresh Token 이 전달되지 않았습니다", null);
        }

        Map<String, String> res = new HashMap<>();

        // Access Token 만료 여부 확인
        if (jwtTokenProvider.isExpired(accessToken)) {
            try {
                Map<String, Object> claims = jwtTokenProvider.validateToken(refreshToken);
                accessToken = jwtTokenProvider.generateToken(claims, JwtConstants.ACCESS_EXP_TIME);
            } catch (JwtTokenException exception) {
                return new ApiResponseDTO<>(BAD_REQUEST, FAIL, "refresh Token 이 만료되었습니다", null);
            }
        }

        res.put("Access Token", accessToken);
        res.put("Refresh Token", refreshToken);

        return new ApiResponseDTO<>(OK, SUCCESS, "access token 갱신 완료", res);
    }
}
