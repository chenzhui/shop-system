package com.example.feign;

import com.example.common.entity.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("shop-seckill")
public interface SeckillFeignApi {
    @PostMapping("/seckillProduct/rollBackStock")
    int rollBackStock(@RequestBody OrderInfo orderInfo);;
}
