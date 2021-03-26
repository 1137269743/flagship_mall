package com.flagship.mall.exception;

/**
 * @Author Flagship
 * @Date 2021/3/24 22:31
 * @Description 统一业务异常
 */
public class FlagshipMallException extends RuntimeException {
    private final Integer code;
    private final String message;

    public FlagshipMallException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public FlagshipMallException(FlagshipMallExceptionEnum exceptionEnum) {
        this(exceptionEnum.getCode(), exceptionEnum.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
