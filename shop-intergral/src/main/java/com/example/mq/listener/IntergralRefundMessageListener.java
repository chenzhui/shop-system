package com.example.mq.listener;

import com.example.common.entity.IntergralRefundVo;
import com.example.common.mq.MQConstant;
import com.example.service.IntergralService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = MQConstant.INTERGRAL_REFUND_TX_GROUP , topic = MQConstant.INTERGRAL_REFUND_TX_TOPIC)
public class IntergralRefundMessageListener implements RocketMQListener<IntergralRefundVo> {
    @Resource
    private IntergralService intergralService;
    @Override
    public void onMessage(IntergralRefundVo intergralRefundVo) {
        log.info("积分事务开始执行");
        intergralService.doRefound(intergralRefundVo);
        log.info("积分事务执行完成");
    }
}
