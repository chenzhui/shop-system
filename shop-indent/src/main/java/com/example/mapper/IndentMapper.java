package com.example.mapper;

import com.example.common.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IndentMapper {

    List<OrderInfo> selectOrderInfoByUserId(Long userId);

    OrderInfo selectOrderInfoByOrderNo(String orderNo);

    int changePayStatus(@Param("orderNo")String orderNo, @Param("status") Integer status, @Param("payType") int payType);

    int changeRefundStatus(@Param("orderNo") String outTradeNo, @Param("status") Integer statusRefund);
}