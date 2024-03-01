package swyg.vitalroutes.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import swyg.vitalroutes.common.response.ApiResponseDTO;
import swyg.vitalroutes.common.response.ResponseType;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


@Slf4j
public class CommonLoginFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.info("--------------------------- CommonLoginFailHandler ---------------------------");

        ApiResponseDTO<Object> apiResponse = new ApiResponseDTO<>(HttpStatus.BAD_REQUEST, ResponseType.FAIL, "아이디 또는 비밀번호가 일치하지 않습니다", null);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(apiResponse);

        response.setStatus(400);
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.println(json);
        writer.close();
    }
}
