package com.example.mq.listener;

import com.example.common.mq.MQConstant;
import com.example.common.entity.IntergralRefundVo;
import com.example.common.entity.OrderInfo;
import com.example.mapper.OrderInfoMapper;
import com.example.service.OrderInfoService;
import com.example.service.SeckillProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@RocketMQMessageListener(consumerGroup = MQConstant.INTERGRAL_REFUND_TX_GROUP, topic = MQConstant.INTERGRAL_REFUND_TX_TOPIC)
@Component
@Slf4j
public class IntergralRefundMessageListener implements RocketMQListener<IntergralRefundVo> {
    @Resource
    private SeckillProductService seckillProductService;
    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Override
    public void onMessage(IntergralRefundVo intergralRefundVo) {
        OrderInfo orderInfo=orderInfoMapper.selectOrderInfoByNo(intergralRefundVo.getOrderNo());
        seckillProductService.incryStockCount(orderInfo.getSeckillId());
        log.info("库存事务执行完成");
    }
}
