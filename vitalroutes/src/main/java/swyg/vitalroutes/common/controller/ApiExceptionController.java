package swyg.vitalroutes.common.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import swyg.vitalroutes.common.exception.JwtTokenException;
import swyg.vitalroutes.common.exception.KakaoLoginException;
import swyg.vitalroutes.common.exception.MemberModifyException;
import swyg.vitalroutes.common.exception.MemberSignUpException;
import swyg.vitalroutes.common.response.ApiResponseDTO;


@RestControllerAdvice
public class ApiExceptionController {
    @ExceptionHandler(MemberSignUpException.class)
    public ApiResponseDTO<?> memberSignUpEx(MemberSignUpException exception) {
        return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
    }

    @ExceptionHandler(JwtTokenException.class)
    public ApiResponseDTO<?> jwtTokenEx(JwtTokenException exception) {
        return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
    }

    @ExceptionHandler(KakaoLoginException.class)
    public ApiResponseDTO<?> kakaoLoginEx(KakaoLoginException exception) {
        return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
    }

    @ExceptionHandler(MemberModifyException.class)
    public ApiResponseDTO<?> memberModifyEx(MemberModifyException exception) {
        return new ApiResponseDTO<>(exception.getStatus(), exception.getType(), exception.getMessage(), null);
    }
}
