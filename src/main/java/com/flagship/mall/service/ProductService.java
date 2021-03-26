package com.flagship.mall.service;

import com.flagship.mall.model.request.AddProductReq;

/**
 * @Author Flagship
 * @Date 2021/3/25 9:05
 * @Description 商品service
 */
public interface ProductService {
    /**
     * 新增商品
     * @param addProductReq 新增数据对象
     */
    void add(AddProductReq addProductReq);
}
