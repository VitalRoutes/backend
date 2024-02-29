package swyg.vitalroutes.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import swyg.vitalroutes.common.exception.JwtTokenException;
import swyg.vitalroutes.security.utils.JwtConstants;
import swyg.vitalroutes.security.utils.JwtTokenProvider;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
public class JwtVerifyFilter extends OncePerRequestFilter {

    private static final String[] whitelist = {"/", "/member/duplicateCheck" ,"/member/signUp", "/member/login", "/token/refresh", "/post/**",
            "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**"};

    // 필터를 거치지 않을 URL 을 설정하고, true 를 return 하면 현재 필터를 건너뛰고 다음 필터로 이동
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        log.info("uri = {}", requestURI);
        return PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("--------------------------- JwtVerifyFilter ---------------------------");

        String authHeader = request.getHeader(JwtConstants.JWT_HEADER);

        try {
            JwtTokenProvider.checkAuthorizationHeader(authHeader);
            String token = JwtTokenProvider.getTokenFromHeader(authHeader); // Bearer 을 생략하고 토큰만 추출
            Authentication authentication = JwtTokenProvider.getAuthentication(token);

            log.info("authentication = {}", authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);    // 다음 필터로 이동
        } catch (JwtTokenException exception) {

            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(exception.getStatusCode());

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(Map.of("error", exception.getMessage()));

            PrintWriter printWriter = response.getWriter();
            printWriter.println(json);
            printWriter.close();
        }
    }
}
