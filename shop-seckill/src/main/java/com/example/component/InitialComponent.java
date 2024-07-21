package com.example.component;

import com.example.redis.SeckillRedisKey;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InitialComponent implements ApplicationRunner {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String stockCountKey="seckill:stockCount:"+18;
        stringRedisTemplate.opsForHash().increment(stockCountKey,2+"",81);
    }
}
