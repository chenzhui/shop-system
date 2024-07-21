package com.example.mq;

import com.example.common.entity.LoginLog;
import com.example.mapper.UserMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@RocketMQMessageListener(consumerGroup = "LoginLogGroup",topic = "LOGIN_TOPIC" )
public class MQLoginLogListener implements RocketMQListener<LoginLog> {
    @Resource
    private UserMapper userMapper;
    @Override
    public void onMessage(LoginLog message) {
        userMapper.insertLoginLog(message);//通过MQ进行异步登录日志记录
    }
}
