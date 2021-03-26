package com.flagship.mall.controller;

import com.flagship.mall.common.ApiRestResponse;
import com.flagship.mall.common.Constant;
import com.flagship.mall.exception.FlagshipMallExceptionEnum;
import com.flagship.mall.model.pojo.Category;
import com.flagship.mall.model.pojo.User;
import com.flagship.mall.model.request.AddCategoryReq;
import com.flagship.mall.model.request.UpdateCategoryReq;
import com.flagship.mall.model.vo.CategoryVO;
import com.flagship.mall.service.CategoryService;
import com.flagship.mall.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

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
     * 后台新增一个分类
     * @param addCategoryReq 传入参数
     * @param session session对象
     * @return 统一响应对象
     */
    @ApiOperation("后台新增分类")
    @PostMapping("/admin/category")
    public ApiRestResponse addCategory(@Valid @RequestBody AddCategoryReq addCategoryReq, HttpSession session) {
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

    @ApiOperation("后台更新分类")
    @PutMapping("/admin/category/{id:\\d+}")
    public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq, HttpSession session) {
        //校验是否登录
        User currentUser = (User) session.getAttribute(Constant.FLAGSHIP_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_LOGIN);
        }
        //校验是否是管理员
        if (userService.checkAdminRole(currentUser)) {
            //是管理员，执行操作
            Category category = new Category();
            BeanUtils.copyProperties(updateCategoryReq, category);
            categoryService.update(category);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(FlagshipMallExceptionEnum.NEED_ADMIN);
        }
    }

    /**
     * 删除分类
     * @param id 分类id
     * @return 统一响应对象
     */
    @ApiOperation("后台删除分类")
    @DeleteMapping("/admin/category/{id:\\d+}")
    public ApiRestResponse deleteCategory(@PathVariable("id") Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台分类列表")
    @GetMapping("/admin/categories")
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo<Category> pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("用户分类列表")
    @GetMapping("/categories")
    public ApiRestResponse listCategoryForCustomer() {
        List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(categoryVOList);
    }
}
