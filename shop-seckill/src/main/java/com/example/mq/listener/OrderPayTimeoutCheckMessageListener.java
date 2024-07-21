package com.example.mq.listener;

import com.alibaba.fastjson.JSON;
import com.example.mq.MQConstant;
import com.example.mq.OrderTimeoutMessage;
import com.example.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@RocketMQMessageListener(
        consumerGroup = MQConstant.ORDER_PAY_TIMEOUT_GROUP,
        topic = MQConstant.ORDER_PAY_TIMEOUT_TOPIC
)
@Component
@Slf4j
public class OrderPayTimeoutCheckMessageListener implements RocketMQListener<OrderTimeoutMessage> {

    @Resource
    private OrderInfoService orderInfoService;

    @Override
    public void onMessage(OrderTimeoutMessage message) {
        log.info("[超时未支付检查] 收到超时未支付检查消息：{}", JSON.toJSONString(message));
        orderInfoService.checkPyTimeout(message);
    }
}
