package com.example.service;

import com.example.common.result.Result;

import com.example.common.entity.IntergralPayVo;
import com.example.common.entity.OrderInfo;

import java.util.List;

public interface IndentSevice {


    List<OrderInfo> selectOrderInfoByUserId(String token);
    String payForZFB(String orderNo);
    String payForIntergral(String orderNo,String token);

    Result doIntergralPay(IntergralPayVo intergralPayVo);

    OrderInfo selectOrderInfoByOrderNo(String orderNo);
    void alipaySuccess(String orderNo, String tradeNo, String totalAmount);

    void refund(String orderNo, String token);

    void changeRefundStatus(OrderInfo orderInfo, String reason);

}
