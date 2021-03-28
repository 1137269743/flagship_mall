package com.flagship.mall.service;


import com.flagship.mall.model.vo.CartVO;

import java.util.List;

/**
 * @Author Flagship
 * @Date 2021/3/24 8:58
 * @Description 用户Service
 */
public interface CartService {
    /**
     * 添加购物车
     * @param userId 用户id
     * @param productId 商品id
     * @param count 数量
     * @return 购物车列表
     */
    List<CartVO> add(Integer userId, Integer productId, Integer count);

    /**
     * 返回购物车列表
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<CartVO> list(Integer userId);

    /**
     * 更新购物车
     * @param userId 用户id
     * @param productId 商品id
     * @param count 数量
     * @return 购物车列表
     */
    List<CartVO> update(Integer userId, Integer productId, Integer count);

    /**
     * 删除购物车
     * @param userId 用户id
     * @param productId 商品id
     * @return 购物车列表
     */
    List<CartVO> delete(Integer userId, Integer productId);

    /**
     * 更改购物车选中状态
     * @param userId 用户id
     * @param cartId 购物车id
     * @param selected 选中状态
     * @return 购物车列表
     */
    List<CartVO> selectOrNot(Integer userId, Integer cartId, Integer selected);
}
