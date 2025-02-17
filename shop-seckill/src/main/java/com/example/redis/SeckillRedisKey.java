package com.example.redis;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * Created by wolfcode
 */
@Getter
public enum SeckillRedisKey {
    SECKILL_PRODUCT_LIST("today:seckill:products:"),
    SECKILL_ORDER_HASH("seckill:OrderHash:"),
    SECKILL_STOCK_COUNT_HASH("seckill:stockCount:"),
    SECKILL_REAL_COUNT_HASH("seckill:RealCount:");

    SeckillRedisKey(String prefix, TimeUnit unit, int expireTime) {
        this.prefix = prefix;
        this.unit = unit;
        this.expireTime = expireTime;
    }

    SeckillRedisKey(String prefix) {
        this.prefix = prefix;
    }

    public String join(String key) {
        return this.prefix + key;
    }

    private String prefix;
    private TimeUnit unit;
    private int expireTime;
}
