package swyg.vitalroutes.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.security.domain.UserDetailsImpl;
import swyg.vitalroutes.security.utils.JwtConstants;
import swyg.vitalroutes.security.utils.JwtTokenProvider;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
public class CommonLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("--------------------------- CommonLoginSuccessHandler ---------------------------");

        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        Member member = principal.getMember();

        Map<String, Object> claims = member.getClaims();
        claims.put("accessToken", JwtTokenProvider.generateToken(claims, JwtConstants.ACCESS_EXP_TIME));
        claims.put("refreshToken", JwtTokenProvider.generateToken(claims, JwtConstants.REFRESH_EXP_TIME));

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(claims);

        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.println(json);
        writer.close();
    }
}
