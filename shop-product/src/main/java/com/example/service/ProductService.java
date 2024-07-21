package com.example.service;

import com.example.common.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductService {
    List<Product> queryProductByIds(@Param("ids") List<Long> ids);
}
