package cn.hyy.order.service;


import cn.hyy.order.dto.PlaceOrderRequest;
import cn.hyy.order.enums.OrderStatus;

/**
 * @Description: 订单接口
 * @author: zyf
 * @date: 2022/01/24
 * @version: V1.0
 */
public interface SeataOrderService {
    /**
     * 下单
     *
     * @param placeOrderRequest 订单请求参数
     */
    void placeOrder(PlaceOrderRequest placeOrderRequest);

    void placeOrderWithNoPay(PlaceOrderRequest request);

    Boolean updateOrderStatus(Integer orderId, OrderStatus success);
}
