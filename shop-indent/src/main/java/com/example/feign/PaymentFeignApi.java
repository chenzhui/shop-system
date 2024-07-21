package com.example.feign;

import com.example.common.result.Result;
import com.example.common.entity.PayVo;
import com.example.common.entity.RefundVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("shop-pay")
public interface PaymentFeignApi {

    @PostMapping("/alipay/doPay")
    Result<String> doPay(@RequestBody PayVo vo);


    @PostMapping("/alipay/checkRSASignature")
    Result<Boolean> checkRSASignature(@RequestBody Map<String, String> params);

    @PostMapping("/alipay/refund")
    Result<Boolean> refund(@RequestBody RefundVo vo);
}
