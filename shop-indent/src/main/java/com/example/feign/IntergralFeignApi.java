package com.example.feign;

import com.example.common.result.Result;
import com.example.common.entity.IntergralPayVo;
import com.example.common.entity.IntergralRefundVo;
import com.example.common.entity.PayVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("shop-intergral")
public interface IntergralFeignApi {

    @PostMapping("/igPay/doPay")
    String doPay(@RequestBody IntergralPayVo intergralPayVo);

    @PostMapping("/igPay/doRefund")
    String doRefund(@RequestBody IntergralRefundVo intergralRefundVo);

}
