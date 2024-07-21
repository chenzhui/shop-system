package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.example.common.CommonConstants;
import com.example.common.anno.RequireLogin;
import com.example.common.entity.OrderInfo;
import com.example.common.entity.SeckillProductVo;
import com.example.common.entity.UserInfo;
import com.example.common.redis.CommonRedisKey;
import com.example.common.result.Result;
import com.example.common.utils.ResultUtil;
import com.example.mapper.SeckillProductMapper;
import com.example.service.OrderInfoService;
import com.example.service.SeckillProductService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController()
@RequestMapping("seckillProduct")
public class SeckillProductController {

    @Resource
    private SeckillProductService seckillProductService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private OrderInfoService orderInfoService;
    @Resource
    private SeckillProductMapper seckillProductMapper;

    @RequestMapping("/queryByTime")
    public Result<List<SeckillProductVo>> queryByTime(Integer time) {
        return ResultUtil.success(seckillProductService.selectTodayListByTimeFromRedis(time));
    }

    @RequireLogin
    @GetMapping("/findOrderInfoByNo")
    public Result<OrderInfo> findById(String orderNo, @RequestHeader(CommonConstants.TOKEN_NAME) String token) {
        UserInfo userInfo = JSON.parseObject(stringRedisTemplate.opsForValue().get(CommonRedisKey.USER_TOKEN.getRealKey(token)), UserInfo.class);
        OrderInfo orderInfo = orderInfoService.findByOrderNo(orderNo, userInfo.getPhone());
        return ResultUtil.success(orderInfo);
    }

    @PostMapping("/rollBackStock")
    int rollBackStock(@RequestBody OrderInfo orderInfo){
        Long seeckillId=orderInfo.getSeckillId();
        return seckillProductMapper.incrStock(seeckillId);
    };



}
