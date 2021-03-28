package com.flagship.mall.controller;

import com.flagship.mall.common.ApiRestResponse;
import com.flagship.mall.filter.UserFilter;
import com.flagship.mall.model.pojo.User;
import com.flagship.mall.model.request.CreateOrderReq;
import com.flagship.mall.model.vo.OrderVO;
import com.flagship.mall.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Flagship
 * @Date 2021/3/28 15:47
 * @Description 订单控制器
 */
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    /**
     * 创建订单
     * @param createOrderReq 订单参数
     * @return 统一返回对象
     */
    @ApiOperation("创建订单")
    @PostMapping("/order")
    public ApiRestResponse create(@RequestBody CreateOrderReq createOrderReq) {
        User currentUser = UserFilter.currentUser;
        String orderNo = orderService.create(currentUser.getId(), createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    /**
     * 订单详情
     * @param orderNo 订单号
     * @return 统一返回对象
     */
    @ApiOperation("订单详情")
    @GetMapping("/order")
    public ApiRestResponse detail(@RequestParam String orderNo) {
        User currentUser = UserFilter.currentUser;
        OrderVO orderVO = orderService.detail(orderNo, currentUser.getId());
        return ApiRestResponse.success(orderVO);
    }

    /**
     * 获取前台订单列表
     * @param pageNum 页码
     * @param pageSize 每页的数量
     * @return 统一返回对象
     */
    @ApiOperation("前台订单列表")
    @GetMapping("/orders")
    public ApiRestResponse listForCustomer(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        User currentUser = UserFilter.currentUser;
        PageInfo pageInfo = orderService.listForCustomer(currentUser.getId(), pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    /**
     * 前台取消订单
     * @param orderNo 订单号
     * @return 统一返回对象
     */
    @ApiOperation("前台取消订单")
    @PutMapping("/order/cancel")
    public ApiRestResponse cancel(@RequestParam String orderNo) {
        User currentUser = UserFilter.currentUser;
        orderService.cancel(currentUser.getId(), orderNo);
        return ApiRestResponse.success();
    }

    /**
     * 生成支付二维码
     * @param orderNo 订单号
     * @return 统一返回对象
     */
    @ApiOperation("生成支付二维码")
    @GetMapping("/order/qrcode")
    public ApiRestResponse qrcode(@RequestParam String orderNo) {
        String pngAddress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddress);
    }

    /**
     * 支付接口
     * @param orderNo 订单号
     * @return 统一返回对象
     */
    @ApiOperation("支付接口")
    //@PutMapping("/order/pay")
    @GetMapping("/order/pay")
    public ApiRestResponse pay(@RequestParam String orderNo) {
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }

    /**
     * 完结订单
     * @param orderNo 订单号
     * @return 统一返回对象
     */
    @ApiOperation("完结订单")
    @PutMapping("/order/finish")
    public ApiRestResponse finish(@RequestParam String orderNo) {
        User currentUser = UserFilter.currentUser;
        orderService.finish(orderNo, currentUser);
        return ApiRestResponse.success();
    }
}
