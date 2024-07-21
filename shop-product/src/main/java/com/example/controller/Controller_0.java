package com.example.controller;

import com.example.common.entity.Product;
import com.example.common.result.Result;
import com.example.common.utils.ResultUtil;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/productQuery")
public class Controller_0 {
    @Autowired
    private ProductService productService;

    @RequestMapping("/selectByIdList")
    public Result<List<Product>> selectByIdList(@RequestParam("ids") List<Long> idList) {
        return ResultUtil.success(productService.queryProductByIds(idList));
    }




}
