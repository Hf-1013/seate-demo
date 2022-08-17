package cn.hyy.account.mapper;

import cn.hyy.account.entity.SeataAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;


/**
 * @Description: TODO
 * @author: zyf
 * @date: 2022/01/24
 * @version: V1.0
 */
@Mapper
public interface SeataAccountMapper extends BaseMapper<SeataAccount> {


    @Update("update account set balance = balance + ${money} where id = #{userId}")
    int refund(@Param("userId") Long userId, @Param("money") BigDecimal money);

    @Update("update account set balance = balance - ${money} where id = #{userId}")
    int deduct(@Param("userId") Long userId, @Param("money") BigDecimal money);
}