package com.chatmatchingservice.springchatmatching.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {

        ErrorCode code = e.getErrorCode();

        ErrorResponse response = new ErrorResponse(
                code.getStatus().value(),
                code.getCode(),
                code.getMessage()
        );

        return ResponseEntity.status(code.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("[GlobalException] 에러 발생", e);

        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

        ErrorResponse response = new ErrorResponse(
                code.getStatus().value(),
                code.getCode(),
                code.getMessage()
        );

        return ResponseEntity.status(code.getStatus()).body(response);
    }
}
