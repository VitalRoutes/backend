package swyg.vitalroutes.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import swyg.vitalroutes.common.exception.JwtTokenException;
import swyg.vitalroutes.security.utils.JwtConstants;
import swyg.vitalroutes.security.utils.JwtTokenProvider;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class TokenRefreshController {
    @PostMapping("/token/refresh")
    public ResponseEntity<?> tokenRefresh(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, String> body) {
        JwtTokenProvider.checkAuthorizationHeader(authHeader);
        String accessToken = JwtTokenProvider.getTokenFromHeader(authHeader);
        String refreshToken = body.get("refreshToken");

        log.info("refresh Token = {}", refreshToken);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "refresh Token 이 전달되지 않았습니다"));
        }

        Map<String, String> res = new HashMap<>();

        // Access Token 만료 여부 확인
        if (JwtTokenProvider.isExpired(accessToken)) {
            try {
                Map<String, Object> claims = JwtTokenProvider.validateToken(refreshToken);
                accessToken = JwtTokenProvider.generateToken(claims, JwtConstants.ACCESS_EXP_TIME);
            } catch (JwtTokenException exception) {
                throw new JwtTokenException(400, "Refresh Token 이 만료되었습니다");
            }
        }

        res.put("Access Token", accessToken);
        res.put("Refresh Token", refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
