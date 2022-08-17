package cn.hyy.order.controller;

/**
 * @Description: TODO
 * @author: zyf
 * @date: 2022/01/24
 * @version: V1.0
 */


import cn.hyy.order.dto.PlaceOrderRequest;
import cn.hyy.order.service.SeataOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/seata/order")
public class SeataOrderController {

    @Autowired
    private SeataOrderService orderService;

    /**
     * 自由下单
     */
    @PostMapping("/placeOrder")
    public String placeOrder(@Validated @RequestBody PlaceOrderRequest request) {
        orderService.placeOrder(request);
        return "下单成功";
    }



    @PostMapping("/place-order-with-no-pay")
    public String placeOrderWithNoPay(@Validated @RequestBody PlaceOrderRequest request){
        orderService.placeOrderWithNoPay(request);
        return "下单成功";
    }
}