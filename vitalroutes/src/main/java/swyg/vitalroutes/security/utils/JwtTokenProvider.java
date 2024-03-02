package swyg.vitalroutes.security.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import swyg.vitalroutes.common.exception.JwtTokenException;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.security.domain.UserDetailsImpl;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;

public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    public String secretKey;

    // Bearer XXX 형태로 토큰이 전달되는지 체크
    public void checkAuthorizationHeader(String header) {
        if(header == null) {
            throw new JwtTokenException(BAD_REQUEST, FAIL, "토큰이 전달되지 않았습니다");
        } else if (!header.startsWith(JwtConstants.JWT_TYPE)) {
            throw new JwtTokenException(BAD_REQUEST, FAIL, "올바르지 않은 토큰 형식입니다");
        }
    }


    // 헤더에 "Bearer XXX" 형식으로 담겨온 토큰을 추출한다
    public String getTokenFromHeader(String header) {
        return header.split(" ")[1];
    }

    public String generateToken(Map<String, Object> claims, int validTime) {
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        } catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return Jwts.builder()
                .setHeader(Map.of("typ","JWT"))
                .setClaims(claims)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(validTime).toInstant()))
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {

        Map<String, Object> claims = validateToken(token);

        String memberId = String.valueOf(claims.get("memberId"));
        String profile = String.valueOf(claims.get("profile"));
        String name = String.valueOf(claims.get("name"));
        String nickname = String.valueOf(claims.get("nickname"));
        String email = String.valueOf(claims.get("email"));

        Member member = Member.builder()
                .memberId(Long.parseLong(memberId))
                .profile(profile).name(name)
                .nickname(nickname)
                .email(email)
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(member);
        return new UsernamePasswordAuthenticationToken(member, "", userDetails.getAuthorities());
    }

    public Map<String, Object> validateToken(String token) {
        Map<String, Object> claim = null;
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                    .getBody();
        } catch(ExpiredJwtException expiredJwtException){
            throw new JwtTokenException(UNAUTHORIZED, FAIL, "토큰이 만료되었습니다");
        } catch(Exception e){
            throw new JwtTokenException(BAD_REQUEST, FAIL, "올바르지 않은 토큰 형식입니다");
        }
        return claim;
    }

    /**
     * 토큰 갱신 시, 토큰이 만료되었는지 판단하는 메서드
     * validateToken 은 예외를 던져버리기 때문에 따로 메서드를 생성
     */
    public boolean isExpired(String token) {
        try {
            validateToken(token);
        } catch (JwtTokenException exception) {
            return (exception.getStatus() == UNAUTHORIZED);
        }
        return false;
    }
}
