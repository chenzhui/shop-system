package com.example.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class PayLog {

    public static final int PAY_TYPE_ONLINE = 0;//在线支付
    public static final int PAY_TYPE_INTERGRAL = 1;//积分支付

    public static final int PAY_STATUS_PAYING = 0;//支付状态：支付中
    public static final int PAY_STATUS_SUCCESS = 1;  //支付状态：支付成功
    public static final int PAY_STATUS_FAILED = 2;  //支付状态：支付失败

    private String tradeNo;  //支付流水号
    private String orderNo;  // 订单编号
    private Long notifyTime;  // 更新时间
    private String totalAmount;  // 交易金额
    private int payType; // 支付类型
    private int status;

    public PayLog(String tradeNo,String orderNo,String totalAmount,int payType){
        this.tradeNo=tradeNo;
        this.orderNo=orderNo;
        this.totalAmount=totalAmount;
        this.payType=payType;

    }
}
