package com.flagship.mall.service;

import com.flagship.mall.model.request.AddCategoryReq;

/**
 * @Author Flagship
 * @Date 2021/3/25 9:05
 * @Description 分类目录service
 */
public interface CategoryService {
    /**
     * 添加分类
     * @param addCategoryReq 分类请求对象
     */
    public void add(AddCategoryReq addCategoryReq);
}
