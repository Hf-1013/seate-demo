package cn.hyy.account.service;

import java.math.BigDecimal;

/**
 * @Description: 账户接口
 * @author: zyf
 * @date: 2022/01/24
 * @version: V1.0
 */
public interface SeataAccountService {
    /**
     * 扣减金额
     * @param userId 用户 ID
     * @param amount  扣减金额
     */
    void reduceBalance(Long userId, BigDecimal amount);

    void payment(Long userId, Integer orderId, BigDecimal amount);
}
