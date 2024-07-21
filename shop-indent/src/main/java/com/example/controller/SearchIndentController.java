package com.example.controller;

import com.example.common.CommonConstants;
import com.example.common.anno.RequireLogin;
import com.example.common.result.Result;
import com.example.common.utils.ResultUtil;
import com.example.common.entity.OrderInfo;
import com.example.feign.PaymentFeignApi;
import com.example.service.IndentSevice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/search")
public class SearchIndentController {
    @Resource
    private IndentSevice indentSevice;

    @Resource
    private PaymentFeignApi paymentFeignApi;

    @RequireLogin
    @GetMapping("findMyIndents")
    public Result findMyIndents(@RequestHeader(CommonConstants.TOKEN_NAME) String token){
        return ResultUtil.success(indentSevice.selectOrderInfoByUserId(token));
    }

    @RequireLogin
    @GetMapping("findIndentById")
    public Result findIndentById(@RequestParam("orderNo") String orderNo
                                ,@RequestHeader(CommonConstants.TOKEN_NAME) String token){
        return ResultUtil.success(indentSevice.selectOrderInfoByOrderNo(orderNo));
    }


}
