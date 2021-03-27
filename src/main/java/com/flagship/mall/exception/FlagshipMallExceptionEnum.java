package com.flagship.mall.exception;

/**
 * @Author Flagship
 * @Date 2021/3/24 22:12
 * @Description 异常枚举
 */
public enum FlagshipMallExceptionEnum {
    /**
     * 用户名不能为空
     */
    NEED_USER_NAME(10001, "用户名不能为空"),
    NEED_PASSWORD(10002, "密码不能为空"),
    PASSWORD_TOO_SHORT(10003, "密码长度不能小于8位"),
    NAME_EXISTED(10004, "不允许重名"),
    INSERT_FAILED(10005, "插入失败，请重试"),
    WRONG_USER_PASSWORD(10006, "用户名或密码错误"),
    NEED_LOGIN(10007, "用户未登录"),
    UPDATE_FAILED(10008, "更新失败"),
    NEED_ADMIN(10009, "无管理员权限"),
    CREATE_FAILED(10010, "新增失败"),
    REQUEST_PARAM_ERROR(10011, "参数错误"),
    DELETE_FAILED(10012, "删除失败"),
    MKDIR_FAILED(10013, "文件夹创建失败"),
    UPLOAD_FAILED(10014, "上传失败"),
    NOT_SALE(10015, "商品状态不可售"),
    NOT_ENOUGH(10016, "商品库存不足"),
    SYSTEM_ERROR(20000, "系统异常");
    /**
     * 异常码
     */
    Integer code;
    /**
     * 异常信息
     */
    String msg;

    FlagshipMallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
