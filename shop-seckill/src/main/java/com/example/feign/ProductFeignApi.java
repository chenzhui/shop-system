package com.example.feign;

import com.example.common.entity.Product;
import com.example.common.result.Result;
import com.example.feign.fallback.ProductFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "shop-product", fallback = ProductFeignFallback.class)
public interface ProductFeignApi {
    @RequestMapping("/productQuery/selectByIdList")
    Result<List<Product>> selectByIdList(@RequestParam("ids") List<Long> idList);
}
