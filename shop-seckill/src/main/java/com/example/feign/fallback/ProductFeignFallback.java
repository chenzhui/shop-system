package com.example.feign.fallback;

import com.example.common.result.Result;
import com.example.common.entity.Product;
import com.example.feign.ProductFeignApi;

import java.util.List;

public class ProductFeignFallback implements ProductFeignApi {
    @Override
    public Result<List<Product>> selectByIdList(List<Long> idList) {
        return null;
    }
}
