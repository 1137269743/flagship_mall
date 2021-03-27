package com.flagship.mall.controller;

import com.flagship.mall.common.ApiRestResponse;
import com.flagship.mall.filter.UserFilter;
import com.flagship.mall.model.vo.CartVO;
import com.flagship.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Flagship
 * @Date 2021/3/27 23:10
 * @Description 购物车控制器
 */
@RestController
public class CartController {
    @Autowired
    CartService cartService;

    /**
     * 添加商品进购物车
     * @param productId 商品id
     * @param count 数量
     * @return 购物车列表
     */
    @ApiOperation("添加购物车")
    @PostMapping("/cart")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOList = cartService.add(userId, productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    /**
     * 获取当前用户购物车列表
     * @return 购物车列表
     */
    @ApiOperation("购物车列表")
    @GetMapping("/carts")
    public ApiRestResponse list() {
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOList = cartService.list(userId);
        return ApiRestResponse.success(cartVOList);
    }
}
