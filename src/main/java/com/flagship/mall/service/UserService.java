package com.flagship.mall.service;


import com.flagship.mall.exception.FlagshipMallException;
import com.flagship.mall.model.pojo.User;

/**
 * @Author Flagship
 * @Date 2021/3/24 8:58
 * @Description 用户Service
 */
public interface UserService {
    /**
     * 返回一个用户
     * @return 用户对象
     */
    User getUser();

    /**
     * 添加一个用户
     * @param userName 用户名
     * @param password 密码
     * @throws FlagshipMallException 业务异常
     */
    void register(String userName, String password) throws FlagshipMallException;

    /**
     * 检查登录
     * @param userName 用户名
     * @param password 密码
     * @return 用户对象
     * @throws FlagshipMallException 业务异常
     */
    User login(String userName, String password) throws FlagshipMallException;

    /**
     * 更新用户签名
     * @param user 用户对象.
     * @throws  FlagshipMallException 业务异常
     */
    void updateInformation(User user) throws FlagshipMallException;

    /**
     * 校验是否是管理员
     * @param user 用户对象
     * @return 是否
     */
    boolean checkAdminRole(User user);
}
