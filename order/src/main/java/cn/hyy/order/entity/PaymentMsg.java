package cn.hyy.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author houfeng
 * @Date 2022/8/17 15:23
 */
@Data
@TableName("seata_account.payment_msg")
public class PaymentMsg {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer orderId;

    private PaymentMsgStatus status;

    private Integer failCnt;

    private Date createTime;

    private Long userId;

    private Date updateTime;

    public static enum PaymentMsgStatus{
        WAITING_SEND,
        SENT,
        FAIL
    }
}
