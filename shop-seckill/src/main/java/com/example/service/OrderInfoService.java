package com.example.service;

import com.example.common.entity.OrderInfo;
import com.example.common.entity.SeckillProductVo;
import com.example.common.entity.UserInfo;
import com.example.mq.OrderTimeoutMessage;

public interface OrderInfoService {
    String doSeckill(UserInfo userInfo, SeckillProductVo vo);
    void syncStock(Long seckillId, Long userPhone);
    void checkPyTimeout(OrderTimeoutMessage message);
    OrderInfo findByOrderNo(String orderNo, Long userId);
}
