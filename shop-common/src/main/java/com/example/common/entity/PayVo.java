package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayVo {
    private String outTradeNo;//订单编号
    private String totalAmount; //付款金额，必填
    private String subject; //订单名称，必填
    private String body;//商品描述，可空
    private String returnUrl;//同步回调地址
    private String notifyUrl; //异步回调地址



}
