package cn.hyy.order.enums;

/**
 * @Description: 订单状态
 * @author: zyf
 * @date: 2022/01/24
 * @version: V1.0
 */
public enum OrderStatus {
    /**
     * INIT
     */
    INIT,
    /**
     * 待支付
     */
    WAITING_PAY,
    /**
     * SUCCESS
     */
    SUCCESS,
    /**
     * FAIL
     */
    FAIL
}