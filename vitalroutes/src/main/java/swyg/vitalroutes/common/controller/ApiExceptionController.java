package swyg.vitalroutes.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import swyg.vitalroutes.common.exception.JwtTokenException;
import swyg.vitalroutes.common.exception.MemberSignUpException;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionController {
    @ExceptionHandler(MemberSignUpException.class)
    public ResponseEntity<?> memberSignUpEx(MemberSignUpException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<?> jwtTokenEx(JwtTokenException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(Map.of("error", exception.getMessage()));
    }
}
