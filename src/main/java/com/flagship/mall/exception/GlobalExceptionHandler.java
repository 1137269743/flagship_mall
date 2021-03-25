package com.flagship.mall.exception;

import com.flagship.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author Flagship
 * @Date 2021/3/24 22:47
 * @Description 处理统一异常的handler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        log.error("Default Exception: ", e);
        return ApiRestResponse.error(FlagshipMallExceptionEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(FlagshipMallException.class)
    public Object handleFlagshipMallException(FlagshipMallException e) {
        log.error("FlagshipMallException: ", e);
        return ApiRestResponse.error(e.getCode(), e.getMessage());
    }
}
