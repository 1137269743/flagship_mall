package com.flagship.mall.controller;

import com.flagship.mall.common.ApiRestResponse;
import com.flagship.mall.common.Constant;
import com.flagship.mall.exception.FlagshipMallExceptionEnum;
import com.flagship.mall.model.pojo.User;
import com.flagship.mall.model.request.AddCategoryReq;
import com.flagship.mall.service.CategoryService;
import com.flagship.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @Author Flagship
 * @Date 2021/3/25 8:50
 * @Description
 */
@RestController
public class CategoryController {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增一个分类
     * @param addCategoryReq 传入参数
     * @param session session对象
     * @return 统一响应对象
     */
    @PostMapping("/admin/category/add")
    public ApiRestResponse addCategory(@RequestBody AddCategoryReq addCategoryReq, HttpSession session) {
        //校验参数完整
        if (addCategoryReq.getName() == null || addCategoryReq.getType() == null || addCategoryReq.getParentId() == null || addCategoryReq.getOrderNum() == null) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.PARA_NOT_NULL);
        }
        //校验是否登录
        User currentUser = (User) session.getAttribute(Constant.FLAGSHIP_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_LOGIN);
        }
        //校验是否是管理员
        if (userService.checkAdminRole(currentUser)) {
            //是管理员，执行操作
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_ADMIN);
        }
    }
}
