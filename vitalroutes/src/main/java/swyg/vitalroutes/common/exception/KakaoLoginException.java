package swyg.vitalroutes.common.exception;

import lombok.Getter;

@Getter
public class KakaoLoginException extends RuntimeException{
    public int statusCode;

    public KakaoLoginException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
