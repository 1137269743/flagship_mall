package com.flagship.mall.service.impl;

import com.flagship.mall.common.Constant;
import com.flagship.mall.exception.FlagshipMallException;
import com.flagship.mall.exception.FlagshipMallExceptionEnum;
import com.flagship.mall.model.dao.CartMapper;
import com.flagship.mall.model.dao.OrderItemMapper;
import com.flagship.mall.model.dao.OrderMapper;
import com.flagship.mall.model.dao.ProductMapper;
import com.flagship.mall.model.pojo.Order;
import com.flagship.mall.model.pojo.OrderItem;
import com.flagship.mall.model.pojo.Product;
import com.flagship.mall.model.pojo.User;
import com.flagship.mall.model.request.CreateOrderReq;
import com.flagship.mall.model.vo.CartVO;
import com.flagship.mall.model.vo.OrderItemVO;
import com.flagship.mall.model.vo.OrderVO;
import com.flagship.mall.service.CartService;
import com.flagship.mall.service.OrderService;
import com.flagship.mall.service.UserService;
import com.flagship.mall.util.OrderCodeFactory;
import com.flagship.mall.util.QrCodeGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author Flagship
 * @Date 2021/3/28 15:48
 * @Description 订单Service实现类
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    CartService cartService;
    @Autowired
    UserService userService;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Value("${file.upload.ip}")
    String ip;

    /**
     * 生成订单
     * @param userId 用户id
     * @param createOrderReq 订单参数
     * @return 订单号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(Integer userId, CreateOrderReq createOrderReq) {
        //判断购物车中已勾选的商品是否为空
        List<CartVO> cartVOList = cartService.list(userId);
        ArrayList<CartVO> cartVOListChecked = new ArrayList<>();
        for (CartVO cartVO : cartVOList) {
            if (cartVO.getSelected().equals(Constant.CartSelected.CHECKED)) {
                cartVOListChecked.add(cartVO);
            }
        }
        if (CollectionUtils.isEmpty(cartVOListChecked)) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.CART_SELECTED_EMPTY);
        }
        //判断商品是否存在、上下架状态、库存
        validSaleStatusAndStock(cartVOListChecked);
        //把购物车对象转为订单item对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOListChecked);
        //扣库存
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new FlagshipMallException(FlagshipMallExceptionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        //删除购物车中已勾选的商品
        cleanCart(cartVOListChecked);
        //生成订单及订单号
        Order order = new Order();
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(BigDecimal.ZERO);
        order.setPaymentType(1);
        //插入到Order表
        if (orderMapper.insertSelective(order) == 0) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.CREATE_FAILED);
        }
        //循环保存每个商品到order_item表
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
            int count = orderItemMapper.insertSelective(orderItem);
            if (count == 0) {
                throw new FlagshipMallException(FlagshipMallExceptionEnum.CREATE_FAILED);
            }
        }
        return orderNo;
    }

    /**
     * 计算订单项的总价格
     * @param orderItemList 订单项列表
     * @return 总价格
     */
    private BigDecimal totalPrice(List<OrderItem> orderItemList) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItemList) {
            totalPrice = totalPrice.add(orderItem.getTotalPrice());
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    /**
     * 根据已勾选的购物车商品列表生成订单项
     * @param cartVOListChecked 已勾选的购物车商品列表
     * @return 订单项列表
     */
    private List<OrderItem> cartVOListToOrderItemList(ArrayList<CartVO> cartVOListChecked) {
        ArrayList<OrderItem> orderItemList = new ArrayList<>();
        for (CartVO cartVO : cartVOListChecked) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            //记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    /**
     * 验证商品是否存在、上架、库存足够
     * @param cartVOList 购物车列表
     */
    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            //判断商品是否存在 是否上架
            if (product == null || product.getStatus().equals(Constant.ProductSaleStatus.NOT_SALE)) {
                throw new FlagshipMallException(FlagshipMallExceptionEnum.NOT_SALE);
            }
            //判断商品库存
            if (cartVO.getQuantity() > product.getStock()) {
                throw new FlagshipMallException(FlagshipMallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    /**
     * 获取订单详情
     * @param orderNo 订单号
     * @param userId 用户id
     * @return 订单详情对象
     */
    @Override
    public OrderVO detail(String orderNo, Integer userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //判断订单是否存在
        if (order == null) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NO_ORDER);
        }
        //判断订单所属
        if (!order.getUserId().equals(userId)) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NOT_YOUR_ORDER);
        }
        OrderVO orderVO = getOrderVO(order);
        return orderVO;
    }

    /**
     * 根据订单生成订单详情对象
     * @param order 订单
     * @return 订单详情对象
     */
    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        //获取订单的OrderItemVOList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        ArrayList<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    /**
     * 查询订单列表（用户）
     * @param userId 用户id
     * @param pageNum 页码
     * @param pageSize 每页的条数
     * @return 分页对象
     */
    @Override
    public PageInfo listForCustomer(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderVOList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    /**
     * 根据订单列表获取订单VOList
     * @param orderList 订单列表
     * @return 订单VOList
     */
    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    /**
     * 取消订单
     * @param userId 用户id
     * @param orderNo 订单号
     */
    @Override
    public void cancel(Integer userId, String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NO_ORDER);
        }
        if (!order.getUserId().equals(userId)) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NOT_YOUR_ORDER);
        }
        if (!order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.WRONG_ORDER_STATUS);
        }
        order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
        order.setEndTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        if (count == 0) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 生成支付二维码
     * @param orderNo 订单编号
     * @return 二维码地址
     */
    @Override
    public String qrcode(String orderNo) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String address = ip + ":" + request.getLocalPort();
        String payUrl = "http://" + address + "/order/pay?orderNo=" + orderNo;
        try {
            QrCodeGenerator.generateQrCodeImage(payUrl, 350, 350, Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pngAddress = "http://" + address + "/images/" + orderNo + ".png";
        return pngAddress;
    }

    /**
     * 查询订单列表（管理员）
     * @param pageNum 页码
     * @param pageSize 每页的条数
     * @return 分页对象
     */
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderVOList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    /**
     * 进行支付
     * @param orderNo 订单号
     */
    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NO_ORDER);
        }
        if (!order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.WRONG_ORDER_STATUS);
        }
        order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
        order.setPayTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * 订单发货
     * @param orderNo 订单号
     */
    @Override
    public void deliver(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NO_ORDER);
        }
        if (!order.getOrderStatus().equals(Constant.OrderStatusEnum.PAID.getCode())) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.WRONG_ORDER_STATUS);
        }
        order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
        order.setDeliveryTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * 完结订单
     *
     * @param orderNo 订单号
     */
    @Override
    public void finish(String orderNo, User currentUser) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NO_ORDER);
        }
        //判断是否管理员或者对应的订单用户
        if (!userService.checkAdminRole(currentUser) && !order.getUserId().equals(currentUser.getId())) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.NOT_YOUR_ORDER);
        }
        if (!order.getOrderStatus().equals(Constant.OrderStatusEnum.DELIVERED.getCode())) {
            throw new FlagshipMallException(FlagshipMallExceptionEnum.WRONG_ORDER_STATUS);
        }
        order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
        order.setEndTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
    }
}
