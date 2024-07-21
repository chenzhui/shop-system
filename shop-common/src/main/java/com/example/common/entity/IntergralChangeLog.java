package com.example.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class IntergralChangeLog implements Serializable {
    public static final int PAY_TYPE_ONLINE = 0;//在线支付
    public static final int PAY_TYPE_INTERGRAL = 1;//积分支付

    public static final int TYPE_DECR = 0;
    public static final int TYPE_INCR = 1;

    public static final int ACCOUNT_LOG_STATUS_TRY = 0;
    public static final int ACCOUNT_LOG_STATUS_CONFIRM = 1;
    public static final int ACCOUNT_LOG_STATUS_CANCEL = 2;

    private String tradeNo;//支付流水号
    private String orderNo;//业务主键
    private int type;//积分变更类型. 0是减少 1是增加
    private Long amount;//此次变化金额
    private Date gmtTime;//日志插入时间
    private String info;//备注信息
    private String txId;
    private String actionId;
    private Integer status;
    private Long timestamp;

    public IntergralChangeLog(String tradeNo,String orderNo,int type,Long amount,String info){
        this.tradeNo=tradeNo;
        this.orderNo=orderNo;
        this.type=type;
        this.amount=amount;
        this.info=info;
    }

}
