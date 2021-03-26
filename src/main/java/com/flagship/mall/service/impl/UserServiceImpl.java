package com.flagship.mall.service.impl;

import com.flagship.mall.exception.FlagshipMallException;
import com.flagship.mall.exception.FlagshipMallExceptionEnum;
import com.flagship.mall.model.dao.UserMapper;
import com.flagship.mall.model.pojo.User;
import com.flagship.mall.service.UserService;
import com.flagship.mall.util.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

/**
 * @Author Flagship
 * @Date 2021/3/24 8:59
 * @Description 用户Service实现类
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    /**
     * 返回一个用户
     *
     * @return 用户对象
     */
    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }

    /**
     * 添加一个用户
     *
     * @param userName 用户名
     * @param password 密码
     * @throws FlagshipMallException 业务异常
     */
    @Override
    public void register(String userName, String password) throws FlagshipMallException {
        User result = userMapper.selectByName(userName);
        if (result != null) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NAME_EXISTED);
        }

        //写到数据库
        User user = new User();
        user.setUsername(userName);
        try {
            user.setPassword(Md5Utils.getMd5Str(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.INSERT_FAILED);
        }
    }

    /**
     * 检查登录
     * @param userName 用户名
     * @param password 密码
     * @return 用户对象
     * @throws FlagshipMallException 业务异常
     */
    @Override
    public User login(String userName, String password) throws FlagshipMallException {
        String md5Password = null;
        try {
            md5Password = Md5Utils.getMd5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        User user = userMapper.selectLogin(userName, md5Password);
        if (user == null) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.WRONG_USER_PASSWORD);
        }
        return user;
    }

    /**
     * 更新用户签名
     *
     * @param user 用户对象
     * @throws FlagshipMallException 业务异常
     */
    @Override
    public void updateInformation(User user) throws FlagshipMallException {
        int count = userMapper.updateByPrimaryKeySelective(user);
        if (count > 1) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 校验是否是管理员
     * @param user 用户对象
     * @return 是否
     */
    @Override
    public boolean checkAdminRole(User user) {
        return user.getRole().equals(2);
    }
}
