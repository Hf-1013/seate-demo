package cn.hyy.account.service.impl;

import cn.hyy.account.entity.AccountFreeze;
import cn.hyy.account.entity.SeataAccount;
import cn.hyy.account.mapper.AccountFreezeMapper;
import cn.hyy.account.mapper.SeataAccountMapper;
import cn.hyy.account.service.AccountTccService;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;

/**
 * @Author houfeng
 * @Date 2022/8/17 10:42
 */
@Slf4j
@Service
public class AccountTccServiceImpl implements AccountTccService {

    @Autowired
    private SeataAccountMapper seataAccountMapper;
    @Autowired
    private AccountFreezeMapper freezeMapper;

    @Override
    public void deduct(Long userId, BigDecimal money) {
        log.info("=============ACCOUNT START=================");
        String xid = RootContext.getXID();
        // 查询冻结记录，如果有，就是cancel执行过，不能继续执行
        //对于已经空回滚的业务，如果以后继续执行try，就永远不可能confirm或cancel，这就是业务悬挂。应当阻止执行空回滚后的try操作，避免悬挂
        AccountFreeze oldfreeze = freezeMapper.selectById(xid);
        if (oldfreeze != null){
            throw new RuntimeException("扣减库存失败");
        }

        System.out.println("seata全局事务id====================>"+ xid);
        SeataAccount account = seataAccountMapper.selectById(userId);
        Assert.notNull(account, "用户不存在");
        BigDecimal balance = account.getBalance();
        log.info("下单用户{}余额为 {},商品总价为{}", userId, balance, money);

        if (balance.compareTo(money)==-1) {
            log.warn("用户 {} 余额不足，当前余额:{}", userId, balance);
            throw new RuntimeException("余额不足");
        }

        // 扣除
        seataAccountMapper.deduct(userId, money);
        // 记录
        AccountFreeze freeze = new AccountFreeze();
        freeze.setXid(xid);
        freeze.setUserId(userId);
        freeze.setFreezeMoney(money);
        freeze.setState(AccountFreeze.State.TRY);
        freezeMapper.insert(freeze);
    }

    @Override
    public boolean confirm(BusinessActionContext ctx) {
        String xid = ctx.getXid();
        int count = freezeMapper.deleteById(xid);
        return count == 1;
    }

    @Override
    public boolean cancel(BusinessActionContext ctx) {
        String xid = ctx.getXid();
        // 查询冻结记录
        AccountFreeze freeze = freezeMapper.selectById(xid);
        if(null == freeze){
            // try没有执行，需要空回滚
            freeze = new AccountFreeze();
            freeze.setXid(xid);
            freeze.setUserId(((Integer) ctx.getActionContext("userId")).longValue());
            freeze.setFreezeMoney(new BigDecimal(0));
            freeze.setState(AccountFreeze.State.CANCEL);
            freezeMapper.insert(freeze);
            return true;
        }

        // 幂等判断
        if(freeze.getState() == AccountFreeze.State.CANCEL){
            return true;
        }

        // 恢复金额
        seataAccountMapper.refund(freeze.getUserId(), freeze.getFreezeMoney());
        freeze.setFreezeMoney(new BigDecimal(0));
        freeze.setState(AccountFreeze.State.CANCEL);
        int count = freezeMapper.updateById(freeze);
        return count == 1;
    }
}
