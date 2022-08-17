package cn.hyy.account.controller;


import cn.hyy.account.service.AccountTccService;
import cn.hyy.account.service.SeataAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author zyf
 */
@RestController
@RequestMapping("/test/seata/account")
public class SeataAccountController {

    @Autowired
    private SeataAccountService accountService;

    @Autowired
    private AccountTccService accountTccService;

    @PostMapping("/reduceBalance")
    public void reduceBalance(Long userId, BigDecimal amount) {
        //TCC
        accountTccService.deduct(userId, amount);
        //accountService.reduceBalance(userId, amount);
    }
}
