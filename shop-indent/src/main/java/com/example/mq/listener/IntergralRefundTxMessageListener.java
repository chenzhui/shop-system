package com.example.mq.listener;

import com.example.common.entity.IntergralRefundVo;
import com.example.common.entity.OrderInfo;
import com.example.common.entity.RefundLog;
import com.example.mapper.IndentMapper;
import com.example.mapper.RefundLogMapper;
import com.example.service.IndentSevice;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Slf4j
@Component
@RocketMQTransactionListener()
public class IntergralRefundTxMessageListener implements RocketMQLocalTransactionListener {
    @Resource
    private IndentSevice indentSevice;
    @Resource
    private IndentMapper indentMapper;
    @Resource
    private RefundLogMapper refundLogMapper;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        try {
            OrderInfo orderInfo= indentMapper.selectOrderInfoByOrderNo((String) o);
            indentSevice.changeRefundStatus(orderInfo,"积分退款");
            return RocketMQLocalTransactionState.COMMIT;
        }catch(Exception e) {
            throw e;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        try{
            String orderNo=(String) message.getHeaders().get("oderNo");
            RefundLog refundLog=refundLogMapper.selectRefundLog(orderNo);
            if(refundLog!=null){
                return RocketMQLocalTransactionState.COMMIT;
            }
        }catch (Exception e){
            throw e;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
