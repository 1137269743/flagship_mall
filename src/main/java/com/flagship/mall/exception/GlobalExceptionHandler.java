package com.flagship.mall.exception;

import com.flagship.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Flagship
 * @Date 2021/3/24 22:47
 * @Description 处理统一异常的handler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 日志对象
     */
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 系统异常处理
     * @param e 系统异常
     * @return 统一异常响应
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        log.error("Default Exception: ", e);
        return ApiRestResponse.error(FlagshipMallExceptionEnum.SYSTEM_ERROR);
    }

    /**
     * 业务异常处理
     * @param e 业务异常
     * @return 统一异常响应
     */
    @ExceptionHandler(FlagshipMallException.class)
    public Object handleFlagshipMallException(FlagshipMallException e) {
        log.error("FlagshipMallException: ", e);
        return ApiRestResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理
     * @param e 参数异常
     * @return 统一异常响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: ", e);
        return handleBindingResult(e.getBindingResult());
    }

    /**
     * 异常结果处理
     * @param result 异常结果
     * @return 统一异常响应
     */
    private ApiRestResponse handleBindingResult(BindingResult result) {
        //把异常处理为对外暴露的提示
        List<String> list = new ArrayList<>();
        if (result.hasErrors()) {
            List<ObjectError> allErrors = result.getAllErrors();
            for (ObjectError allError : allErrors) {
                String message = allError.getDefaultMessage();
                list.add(message);
            }
        }
        if (list.size() == 0) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(FlagshipMallExceptionEnum.REQUEST_PARAM_ERROR.getCode(), list.toString());
    }
}
