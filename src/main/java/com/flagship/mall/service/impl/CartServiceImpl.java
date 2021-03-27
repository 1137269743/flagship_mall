package com.flagship.mall.service.impl;

import com.flagship.mall.common.Constant;
import com.flagship.mall.exception.FlagshipMallException;
import com.flagship.mall.exception.FlagshipMallExceptionEnum;
import com.flagship.mall.model.dao.CartMapper;
import com.flagship.mall.model.dao.ProductMapper;
import com.flagship.mall.model.pojo.Cart;
import com.flagship.mall.model.pojo.Product;
import com.flagship.mall.model.vo.CartVO;
import com.flagship.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Flagship
 * @Date 2021/3/27 23:19
 * @Description 购物车Service实现类
 */
@Service("cartService")
public class CartServiceImpl implements CartService {
    @Autowired
    ProductMapper productMapper;

    @Autowired
    CartMapper cartMapper;

    /**
     * 添加购物车
     * @param userId 用户id
     * @param productId 商品id
     * @param count 数量
     * @return 购物车列表
     */
    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.CartSelected.CHECKED);
            cartMapper.insertSelective(cart);
        } else {
            cart.setQuantity(count + cart.getQuantity());
            cart.setSelected(Constant.CartSelected.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    /**
     * 对要加进购物车的商品进行校验
     * @param productId 商品id
     * @param count 数量
     */
    private void validProduct(Integer productId, Integer count) {
        Product product = productMapper.selectByPrimaryKey(productId);
        //判断商品是否存在 是否上架
        if (product == null || product.getStatus().equals(Constant.ProductSaleStatus.NOT_SALE)) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NOT_SALE);
        }
        //判断商品库存
        if (count > product.getStock()) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NOT_ENOUGH);
        }
    }

    /**
     * 返回购物车列表
     * @param userId 用户ID
     * @return 购物车列表
     */
    @Override
    public List<CartVO> list(Integer userId) {
        List<CartVO> cartVOS = cartMapper.selectList(userId);
        for (CartVO cartVO : cartVOS) {
            cartVO.setTotalPrice(cartVO.getPrice().multiply(BigDecimal.valueOf(cartVO.getQuantity())));
        }
        return cartVOS;
    }
}
