package com.example.controller;
import com.alibaba.fastjson.JSON;
import com.example.common.CommonConstants;
import com.example.common.anno.RequireLogin;
import com.example.common.entity.SeckillProductVo;
import com.example.common.entity.UserInfo;
import com.example.common.exception.BusinessException;
import com.example.common.redis.CommonRedisKey;
import com.example.common.result.Result;
import com.example.common.utils.ResultUtil;
import com.example.mq.MQConstant;
import com.example.mq.OrderMessage;
import com.example.mq.callback.DefaultMQMessageCallback;
import com.example.msg.SeckillCodeMsg;
import com.example.redis.SeckillRedisKey;
import com.example.service.OrderInfoService;
import com.example.service.SeckillProductService;
import com.example.util.DateUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController()
@RequestMapping("seckillOrder")
public class SeckillOrderController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private SeckillProductService seckillProductService;

    @Resource
    private OrderInfoService orderInfoService;
    public static final Map<Long, Boolean> LOCAL_STOCK_COUNT_FLAG_CACHE = new ConcurrentHashMap<>(); //true则货存为0

    @RequireLogin
    @RequestMapping("doSeckill")
    public Result doSeckill(Integer time, Long seckillId,@RequestHeader(CommonConstants.TOKEN_NAME)String token){
        UserInfo userInfo=this.getUserByToken(token);//获取用户信息
        SeckillProductVo vo = seckillProductService.selectByIdAndTime(seckillId, time);//基于场次+秒杀活动id获取到秒杀商品对象
        if (!DateUtil.isLegalTime(vo.getStartDate(), time)) {
            throw new BusinessException(SeckillCodeMsg.OUT_OF_SECKILL_TIME_ERROR);
        }// 判断时间是否大于开始时间 && 小于 开始时间+2小时
        Boolean flag = LOCAL_STOCK_COUNT_FLAG_CACHE.get(seckillId);// 增加本地缓存判断
        if (flag != null && flag) {
            throw new BusinessException(SeckillCodeMsg.SECKILL_STOCK_OVER);
        }
        int max = 1;  //一个人最多可以秒几次
        String userOrderCountKey = SeckillRedisKey.SECKILL_ORDER_HASH.join(seckillId + "");  //往seckill:orderhash中添加秒杀活动id
        Long increment = stringRedisTemplate.opsForHash().increment(userOrderCountKey, userInfo.getPhone() + "", 1);//记录用户对一个秒杀商品发起几次订单
        if (increment > max) {
            throw new BusinessException(SeckillCodeMsg.REPEAT_SECKILL);
        }
        String stockCountKey = "seckill:stockCount:"+time;//添加场次key
        long remain = stringRedisTemplate.opsForHash().increment(stockCountKey, seckillId + "", -1);
        if (remain < 0) {
            LOCAL_STOCK_COUNT_FLAG_CACHE.put(seckillId, true);
            throw new BusinessException(SeckillCodeMsg.SECKILL_STOCK_OVER);
        }   // 标记当前库存已经售完
        rocketMQTemplate.asyncSend(MQConstant.ORDER_PENDING_TOPIC, new OrderMessage(time, seckillId, token, userInfo.getPhone()), new DefaultMQMessageCallback());
        return ResultUtil.success("成功加入下单队列，正在排队中...");
    }

    private UserInfo getUserByToken(String token) {
        return JSON.parseObject(stringRedisTemplate.opsForValue().get(CommonRedisKey.USER_TOKEN.getRealKey(token)), UserInfo.class);
    }

}
