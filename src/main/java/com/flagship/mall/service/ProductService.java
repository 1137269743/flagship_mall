package com.flagship.mall.service;

import com.flagship.mall.model.pojo.Product;
import com.flagship.mall.model.request.AddProductReq;
import com.flagship.mall.model.request.ProductListReq;
import com.flagship.mall.model.request.UpdateProductReq;
import com.github.pagehelper.PageInfo;

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

    /**
     * 更新商品
     * @param updateProductReq 新增商品对象
     */
    void update(UpdateProductReq updateProductReq);

    /**
     * 删除商品
     * @param id 商品id
     */
    void delete(Integer id);

    /**
     * 状态上下架
     * @param ids 商品id列表
     * @param status 商品状态
     */
    void batchUpdateSellStatus(Integer[] ids, Integer status);

    /**
     * 查询商品列表（管理员）
     * @param pageNum 页号
     * @param pageSize 每页的行数
     * @return 商品列表
     */
    PageInfo<Product> listForAdmin(Integer pageNum, Integer pageSize);

    /**
     * 获取商品细节
     * @param id 商品id
     * @return 商品对象
     */
    Product detail(Integer id);

    /**
     * 查询商品列表（用户）
     * @param productListReq 商品列表请求对象
     * @return 商品列表
     */
    PageInfo<Product> list(ProductListReq productListReq);
}
