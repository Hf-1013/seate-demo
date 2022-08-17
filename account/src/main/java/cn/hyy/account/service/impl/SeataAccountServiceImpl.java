package cn.hyy.account.service.impl;


import cn.hyy.account.entity.PaymentMsg;
import cn.hyy.account.entity.SeataAccount;
import cn.hyy.account.mapper.PaymentMsgMapper;
import cn.hyy.account.mapper.SeataAccountMapper;
import cn.hyy.account.service.SeataAccountService;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: TODO
 * @author: zyf
 * @date: 2022/01/24
 * @version: V1.0
 */
@Slf4j
@Service
public class SeataAccountServiceImpl implements SeataAccountService {
    @Resource
    private SeataAccountMapper accountMapper;
    @Resource
    private PaymentMsgMapper paymentMsgMapper;

    /**
     * 事务传播特性设置为 REQUIRES_NEW 开启新的事务
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void reduceBalance(Long userId, BigDecimal amount) {
        log.info("=============ACCOUNT START=================");
        System.out.println("seata全局事务id====================>"+ RootContext.getXID());
        SeataAccount account = accountMapper.selectById(userId);
        Assert.notNull(account, "用户不存在");
        BigDecimal balance = account.getBalance();
        log.info("下单用户{}余额为 {},商品总价为{}", userId, balance, amount);

        if (balance.compareTo(amount)==-1) {
            log.warn("用户 {} 余额不足，当前余额:{}", userId, balance);
            throw new RuntimeException("余额不足");
        }
        log.info("开始扣减用户 {} 余额", userId);
        BigDecimal currentBalance = account.getBalance().subtract(amount);
        account.setBalance(currentBalance);
        accountMapper.updateById(account);
        log.info("扣减用户 {} 余额成功,扣减后用户账户余额为{}", userId, currentBalance);
        log.info("=============ACCOUNT END=================");
    }

    @Override
    @Transactional
    public void payment(Long userId, Integer orderId, BigDecimal amount) {
        //支付操作
        SeataAccount accountA = accountMapper.selectById(userId);
        if(accountA == null){
            throw new RuntimeException("支付失败");
        }
        if(accountA.getBalance().compareTo(amount) < 0){
            throw new RuntimeException("余额不足");
        }

        accountA.setBalance(accountA.getBalance().subtract(amount));
        accountMapper.updateById(accountA);

        PaymentMsg paymentMsg = new PaymentMsg();
        paymentMsg.setOrderId(orderId);
        paymentMsg.setStatus(PaymentMsg.PaymentMsgStatus.WAITING_SEND); //未发送
        paymentMsg.setFailCnt(0); //失败次数
        paymentMsg.setCreateTime(new Date());
        paymentMsg.setUserId(userId);
        paymentMsg.setUpdateTime(new Date());

        paymentMsgMapper.insert(paymentMsg);
    }
}
