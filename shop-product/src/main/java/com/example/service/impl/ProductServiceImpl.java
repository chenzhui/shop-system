package com.example.service.impl;

import com.example.common.entity.Product;
import com.example.mapper.ProductMapper;
import com.example.service.ProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductMapper productMapper;
    @Override
    public List<Product> queryProductByIds(List<Long> ids) {
        return productMapper.queryProductByIds(ids);
    }
}
