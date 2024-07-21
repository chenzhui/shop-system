package com.example.mq.listener;

import com.alibaba.fastjson.JSON;
import com.example.common.mq.MQConstant;
import com.example.common.mq.OrderMQResult;
import com.example.server.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@RocketMQMessageListener(consumerGroup = MQConstant.ORDER_RESULT_CONSUMER_GROUP,topic = MQConstant.ORDER_RESULT_TOPIC)
@Component
@Slf4j
public class OrderResultMQMessageListener implements RocketMQListener<OrderMQResult> {

    @Override
    public void onMessage(OrderMQResult result) {
        log.info("[订单结果] 收到订单结果消息：{}", JSON.toJSONString(result));
        try {
            int count = 3;
            Session session = null;
            do {
                session = WebSocketServer.SESSION_MAP.get(result.getToken());
                Iterator<Map.Entry<String, Session>> it=WebSocketServer.SESSION_MAP.entrySet().iterator();
                if (session != null) {
                    session.getBasicRemote().sendText(JSON.toJSONString(result));
                    log.info("[订单结果] 消息成功通知到前端用户：{}", result.getToken());
                    break;
                }
                log.warn("[订单结果] 无法获取用户连接信息：{}，count：{}", result.getToken(), count);
                // 拿不到睡 500ms
                TimeUnit.MILLISECONDS.sleep(1000);
                count--;
            } while (count > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
