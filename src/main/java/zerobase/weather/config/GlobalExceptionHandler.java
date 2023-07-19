package zerobase.weather.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 전역의 예외 처리
public class GlobalExceptionHandler { // 전역의 예외를 처리해주는 핸들러

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class) // Exception.class 안에 있는 예외처리만 디룸.
    public Exception handelAllException() {
        System.out.println("error from GlobalExceptionHandler");
        return new Exception();
    }
}
