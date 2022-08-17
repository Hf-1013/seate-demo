package cn.hyy.order.schedule;

import cn.hyy.order.entity.PaymentMsg;
import cn.hyy.order.enums.OrderStatus;
import cn.hyy.order.mapper.PaymentMsgMapper;
import cn.hyy.order.service.SeataOrderService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author houfeng
 * @Date 2022/8/17 15:29
 */
@Service
@Slf4j
public class OrderSchedule {

    @Resource
    private PaymentMsgMapper paymentMsgMapper;

    @Resource
    private SeataOrderService orderService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void orderNotify() throws IOException {
        log.info("orderNotify start");
        List<PaymentMsg> list = paymentMsgMapper.selectList(Wrappers.<PaymentMsg>lambdaQuery().eq(PaymentMsg::getStatus, PaymentMsg.PaymentMsgStatus.WAITING_SEND));
        if (list == null || list.size() == 0) {
            return;
        }
        log.info("waiting update order msg:{}", list);
        for (PaymentMsg paymentMsg : list) {
            Boolean result = orderService.updateOrderStatus(paymentMsg.getOrderId(), OrderStatus.SUCCESS);
            if(result){
                paymentMsg.setStatus(PaymentMsg.PaymentMsgStatus.SENT); //发送成功
                paymentMsgMapper.updateById(paymentMsg);
            }else {
                int failCnt = paymentMsg.getFailCnt();
                failCnt ++;
                paymentMsg.setFailCnt(failCnt);
                if(failCnt > 5){
                    paymentMsg.setStatus(PaymentMsg.PaymentMsgStatus.FAIL); //超过5次，改成失败
                }
                paymentMsgMapper.updateById(paymentMsg);
            }

        }
    }
}
