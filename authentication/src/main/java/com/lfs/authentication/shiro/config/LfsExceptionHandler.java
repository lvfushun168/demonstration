package com.lfs.authentication.shiro.config;


import com.lfs.config.model.OperationException;
import com.lfs.config.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class LfsExceptionHandler {


    @ExceptionHandler(OperationException.class)
    public ResponseEntity operationException(OperationException exception){
        log.info("错误码："+exception.getCode().toString());
        log.info("错误信息："+exception.getMessage());
        Result error = Result.error(exception.getCode(), exception.getMessage());
        return ResponseEntity.ok(error);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity authException(AuthorizationException authorizationException){
        log.info("错误码："+"403");
        log.info("错误信息："+authorizationException.getMessage());
        Result error = Result.error(403, authorizationException.getMessage());
        return ResponseEntity.ok(error);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String message = allErrors.stream().map(s -> s.getDefaultMessage()).collect(Collectors.joining(";"));
        return ResponseEntity.ok(Result.error(10086,message));
    }


}
