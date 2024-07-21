package com.example.common.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class RefundVo implements Serializable {
    private String outTradeNo;//交易订单号
    private String refundAmount;//退款金额
    private String refundReason;//退款原因
}
