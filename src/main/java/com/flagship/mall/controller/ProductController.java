package com.flagship.mall.controller;

import com.flagship.mall.common.ApiRestResponse;
import com.flagship.mall.model.pojo.Product;
import com.flagship.mall.model.request.ProductListReq;
import com.flagship.mall.service.ProductService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Flagship
 * @Date 2021/3/26 21:54
 * @Description
 */
@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @ApiOperation("商品详情")
    @GetMapping("/product/{id}")
    public ApiRestResponse detail(@PathVariable("id") Integer id) {
        Product product = productService.detail(id);
        return ApiRestResponse.success(product);
    }

    @ApiOperation("前台商品列表")
    @GetMapping("/products")
    public ApiRestResponse list(ProductListReq productListReq) {
        PageInfo<Product> list = productService.list(productListReq);
        return ApiRestResponse.success(list);
    }
}
