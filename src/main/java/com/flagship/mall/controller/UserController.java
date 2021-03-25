package com.flagship.mall.controller;


import com.flagship.mall.common.ApiRestResponse;
import com.flagship.mall.common.Constant;
import com.flagship.mall.exception.FlagshipMallException;
import com.flagship.mall.exception.FlagshipMallExceptionEnum;
import com.flagship.mall.model.pojo.User;
import com.flagship.mall.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @Author Flagship
 * @Date 2021/3/24 8:57
 * @Description 用户控制器
 */
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @ApiOperation("测试方法")
    @GetMapping("/test")
    public User personalPage() {
        return userService.getUser();
    }

    /**
     * 注册
     * @param userName 用户名
     * @param password 密码
     * @return 统一响应对象
     * @throws FlagshipMallException 业务异常
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public ApiRestResponse register(@RequestParam("userName") String userName,@RequestParam("password") String password) throws FlagshipMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_PASSWORD);
        }
        //密码长度不能少于8位
        if (password.length() < 8) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(userName, password);
        return ApiRestResponse.success();
    }

    /**
     * 登录
     * @param userName 用户名
     * @param password 密码
     * @param session session对象
     * @return 统一响应对象
     * @throws FlagshipMallException 业务异常
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ApiRestResponse login(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws FlagshipMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        //保存用户信息时，不保存密码
        user.setPassword(null);
        session.setAttribute(Constant.FLAGSHIP_MALL_USER, user);
        return ApiRestResponse.success(user);
    }

    /**
     * 更新个性签名
     * @param signature 个性签名
     * @param session session对象
     * @return 统一响应对象
     * @throws FlagshipMallException 业务异常
     */
    @ApiOperation("更新当前用户个性签名")
    @PutMapping("/user/signature")
    public ApiRestResponse updateUserInfo(@RequestParam String signature, HttpSession session) throws FlagshipMallException {
        User currentUser = (User) session.getAttribute(Constant.FLAGSHIP_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    /**
     * 登出，清除session
     * @param session session对象
     * @return 统一响应对象
     */
    @ApiOperation("用户登出")
    @PostMapping("/user/logout")
    public ApiRestResponse logout(HttpSession session) {
        session.removeAttribute(Constant.FLAGSHIP_MALL_USER);
        return ApiRestResponse.success();
    }

    /**
     * 管理员登录接口
     * @param userName 用户名
     * @param password 密码
     * @param session session对象
     * @return 统一响应对象
     * @throws FlagshipMallException 业务异常
     */
    @ApiOperation("管理员用户注登录")
    @PostMapping("/adminLogin")
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws FlagshipMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        if (userService.checkAdminRole(user)) {
            //是管理员
            //保存用户信息时，不保存密码
            user.setPassword(null);
            session.setAttribute(Constant.FLAGSHIP_MALL_USER, user);
            return ApiRestResponse.success(user);
        } else {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_ADMIN);
        }
    }
}
