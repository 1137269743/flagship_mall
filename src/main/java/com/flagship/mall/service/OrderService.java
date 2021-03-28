package com.flagship.mall.service;

import com.flagship.mall.model.pojo.User;
import com.flagship.mall.model.request.CreateOrderReq;
import com.flagship.mall.model.vo.OrderVO;
import com.github.pagehelper.PageInfo;

/**
 * @Author Flagship
 * @Date 2021/3/28 15:47
 * @Description 订单Service
 */
public interface OrderService {
    /**
     * 生成订单
     * @param userId 用户id
     * @param createOrderReq 订单参数
     * @return 订单号
     */
    String create(Integer userId, CreateOrderReq createOrderReq);

    /**
     * 获取订单详情
     * @param orderNo 订单号
     * @param userId 用户id
     * @return 订单详情对象
     */
    OrderVO detail(String orderNo, Integer userId);

    /**
     * 查询订单列表（用户）
     * @param userId 用户id
     * @param pageNum 页码
     * @param pageSize 每页的条数
     * @return 分页对象
     */
    PageInfo listForCustomer(Integer userId, Integer pageNum, Integer pageSize);

    /**
     * 取消订单
     * @param userId 用户id
     * @param orderNo 订单号
     */
    void cancel(Integer userId, String orderNo);

    /**
     * 生成支付二维码
     * @param orderNo 订单编号
     * @return 二维码地址
     */
    String qrcode(String orderNo);

    /**
     * 查询订单列表（管理员）
     * @param pageNum 页码
     * @param pageSize 每页的条数
     * @return 分页对象
     */
    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    /**
     * 进行支付
     * @param orderNo 订单号
     */
    void pay(String orderNo);

    /**
     * 订单发货
     * @param orderNo 订单号
     */
    void deliver(String orderNo);

    /**
     * 完结订单
     * @param orderNo 订单号
     * @param currentUser 当前用户对象
     */
    void finish(String orderNo, User currentUser);
}
