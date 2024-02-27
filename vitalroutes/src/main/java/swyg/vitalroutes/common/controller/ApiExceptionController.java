package swyg.vitalroutes.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import swyg.vitalroutes.common.exception.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionController {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> fileSizeExceeded() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "파일 용량이 초과하였습니다"));
    }

    // 파일이 이미지 타입이 아닌 경우
    @ExceptionHandler(FileTypeNotMatchException.class)
    public ResponseEntity<?> fileTypeNotMatching(FileTypeNotMatchException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    // 파일 내부에 GPS 정보가 없는 경우
    @ExceptionHandler(FileNotEnoughInformationException.class)
    public ResponseEntity<?> fileNotHaveGpsInfo(FileNotEnoughInformationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    // 파일이 두 개 이상 전달되지 않은 경우
    @ExceptionHandler(FileNotEnoughException.class)
    public ResponseEntity<?> fileNotEnough(FileNotEnoughException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    // MultiPartFile 을 File 로 변환하는데 실패
    @ExceptionHandler(FailedConversionFileException.class)
    public ResponseEntity<?> failedConversionFile(FailedConversionFileException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    // 파일의 메타정보를 읽는데 실패
    @ExceptionHandler(FailedReadImageException.class)
    public ResponseEntity<?> failedReadImage(FailedReadImageException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    // Post 입력값이 맞지 않는 경우
    @ExceptionHandler(InputViolateException.class)
    public ResponseEntity<?> inputIsViolated(InputViolateException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }
}
