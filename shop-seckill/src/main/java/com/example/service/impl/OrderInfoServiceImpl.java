package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.common.entity.OrderInfo;
import com.example.common.entity.SeckillProduct;
import com.example.common.entity.SeckillProductVo;
import com.example.common.entity.UserInfo;
import com.example.common.exception.BusinessException;
import com.example.mapper.OrderInfoMapper;
import com.example.mq.MQConstant;
import com.example.mq.OrderTimeoutMessage;
import com.example.mq.callback.DefaultMQMessageCallback;
import com.example.msg.SeckillCodeMsg;
import com.example.redis.SeckillRedisKey;
import com.example.service.OrderInfoService;
import com.example.service.SeckillProductService;
import com.example.util.IdGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
@Slf4j
@Service
public class OrderInfoServiceImpl implements OrderInfoService {
    @Resource
    private SeckillProductService seckillProductService;
    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String doSeckill(UserInfo userInfo, SeckillProductVo vo) {    //生成订单
        int row = seckillProductService.reduceStockCount(vo.getSeckillId(), vo.getTime());  //在数据库中减少库存
        if (row <= 0) {
            throw new BusinessException(SeckillCodeMsg.SECKILL_STOCK_OVER);
        }
        OrderInfo orderInfo = this.buildOrderInfo(userInfo, vo);
        orderInfoMapper.insertOrder(orderInfo);    //在数据库中添加订单
        return orderInfo.getOrderNo();//返回订单编号
    }


    @Transactional
    @Override
    public void checkPyTimeout(OrderTimeoutMessage message) {
        int row = orderInfoMapper.updateCancelStatus(message.getOrderNo(), OrderInfo.STATUS_TIMEOUT);   //修改订单状态为超时
        if (row > 0) {
            seckillProductService.incryStockCount(message.getSeckillId());    //数据库中库存+1
            this.syncStock(message.getSeckillId(), message.getUserPhone());   //将redis,用户下单标识,库存本地缓存恢复
        }
    }

    @Override
    public void syncStock(Long seckillId, Long userPhone) {
        stringRedisTemplate.opsForHash().delete(SeckillRedisKey.SECKILL_ORDER_HASH.join(seckillId + ""), userPhone + "");// 1. 删除用户重复下单标识
        SeckillProduct seckillProduct = seckillProductService.findById(seckillId);  // 找到秒杀商品
        String key = "seckill:stockCount:"+seckillProduct.getTime();  //redis的对应商品库存的key
        stringRedisTemplate.opsForHash().put(key, seckillProduct.getSeckillId() + "", seckillProduct.getStockCount() + "");//从数据库中取出并放入redis的vaLue
        rocketMQTemplate.asyncSend(MQConstant.CANCEL_SECKILL_OVER_SIGE_TOPIC, seckillId, new DefaultMQMessageCallback());  //将所有的本地缓存改为该商品还有库存
    }

    @Override
    public OrderInfo findByOrderNo(String orderNo, Long userId) {
        OrderInfo orderInfo = null;
        String orderKey = "seckill:OrderHash:"+userId;
        String json = (String) stringRedisTemplate.opsForHash().get(orderKey,orderNo);  //根据订单编号在缓存中查找用户的订单
        if (StringUtils.isEmpty(json)) {   //如果查不到，就从数据库中查询
            OrderInfo orderInfoInDB = orderInfoMapper.selectOrderInfoByNo(orderNo);
            log.info("[订单详情] 从 MySQL 中查询到订单详情数据：{}", JSON.toJSONString(orderInfoInDB));
            if (orderInfoInDB != null && orderInfoInDB.getUserId().equals(userId)) {
                orderInfo = orderInfoInDB;
                stringRedisTemplate.opsForHash().put(orderKey, orderNo, JSON.toJSONString(orderInfoInDB));
            }  //将数据库查询到的数据再次存入缓存
        } else {    // 如果 redis 中可以查到，就解析 redis 的数据并返回
            log.info("[订单详情] 从 Redis 中查询到订单详情数据：{}", json);
            orderInfo = JSON.parseObject(json, OrderInfo.class);
        }
        return orderInfo;
    }





    private OrderInfo buildOrderInfo(UserInfo userInfo, SeckillProductVo vo) {
        Date now = new Date();
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(now);
        orderInfo.setDeliveryAddrId(1L);
        orderInfo.setIntergral(vo.getIntergral());
        orderInfo.setOrderNo(IdGenerateUtil.get().nextId() + "");  //生成订单编号
        orderInfo.setPayType(OrderInfo.PAY_TYPE_ONLINE);
        orderInfo.setProductCount(1);
        orderInfo.setProductId(vo.getProductId());
        orderInfo.setProductImg(vo.getProductImg());
        orderInfo.setProductName(vo.getProductName());
        orderInfo.setProductPrice(vo.getProductPrice());
        orderInfo.setSeckillDate(now);
        orderInfo.setSeckillId(vo.getSeckillId());
        orderInfo.setSeckillPrice(vo.getSeckillPrice());
        orderInfo.setSeckillTime(vo.getTime());
        orderInfo.setStatus(OrderInfo.STATUS_ARREARAGE);
        orderInfo.setUserId(userInfo.getPhone());
        return orderInfo;
    }
}
