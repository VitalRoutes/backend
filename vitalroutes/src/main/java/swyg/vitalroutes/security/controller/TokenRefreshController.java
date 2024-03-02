package swyg.vitalroutes.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(description = "Access Token 을 헤더에 Bearer XXX 형태로, Refresh Token 을 body 에 전달하여 Access Token 을 갱신", summary = "Access Token 갱신")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK",
                    description = "Access Token 과 Refresh Token 이 data 에 담겨서 전달",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "BAD_REQUEST",
                    description = "refresh token 이 전달되지 않음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "UNAUTHORIZED",
                    description = "refresh token 이 만료, 재로그인 필요",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    @Parameters(value = {
            @Parameter(name = "Authorization", required = true, description = "Access Token 을 Bearer 형태로 전달해야함")
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
                return new ApiResponseDTO<>(UNAUTHORIZED, FAIL, "refresh Token 이 만료되었습니다. 다시 로그인해 주세요", null);
            }
        }

        res.put("Access Token", accessToken);
        res.put("Refresh Token", refreshToken);

        return new ApiResponseDTO<>(OK, SUCCESS, "access token 갱신 완료", res);
    }
}
