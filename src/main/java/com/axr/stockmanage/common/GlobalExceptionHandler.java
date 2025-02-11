package com.axr.stockmanage.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author xinrui.an
 * @date 2025/02/11
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<String> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return Result.fail("服务器错误");
    }

}