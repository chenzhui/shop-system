package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.common.entity.Product;
import com.example.common.entity.SeckillProduct;
import com.example.common.entity.SeckillProductVo;
import com.example.common.exception.BusinessException;
import com.example.common.msg.CodeMsg;
import com.example.common.result.Result;
import com.example.feign.ProductFeignApi;
import com.example.mapper.SeckillProductMapper;
import com.example.redis.SeckillRedisKey;
import com.example.service.SeckillProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@CacheConfig(cacheNames="SeckillProduct")
public class SeckillProductServiceImpl implements SeckillProductService {

    @Resource
    private SeckillProductMapper seckillProductMapper;
    @Resource
    private ProductFeignApi productFeignApi;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<SeckillProductVo> selectTodayListByTimeFromRedis(Integer time) {
        String key = SeckillRedisKey.SECKILL_PRODUCT_LIST.join(time + "");
        List<String> stringList = stringRedisTemplate.opsForList().range(key, 0, -1);   //从缓存中得到当前时间段秒杀商品
        if (stringList == null || stringList.size() == 0) {
            log.warn("[秒杀商品] 查询秒杀商品列表异常, Redis 中没有数据, 从 DB 中查询...");
            return this.selectTodayListByTime(time);
        }
        return stringList.stream().map(json -> JSON.parseObject(json, SeckillProductVo.class)).collect(Collectors.toList());
    }

    @Override
    public List<SeckillProductVo> selectTodayListByTime(Integer time) {
        List<SeckillProduct> todayList = seckillProductMapper.queryCurrentlySeckillProduct(time);    //查询当天所有秒杀数据
        if (todayList.size() == 0) {return Collections.emptyList();}  //为空则直接返回
        List<Long> productIdList = todayList.stream().map(SeckillProduct::getProductId).distinct().collect(Collectors.toList());//遍历秒杀商品列表, 得到商品 id 列表
        Result<List<Product>> result = productFeignApi.selectByIdList(productIdList);  //得到商品列表的Result格式
        if (result.hasError() || result.getData() == null) {throw new BusinessException(new CodeMsg(result.getStatusCode(), result.getMsg()));}  //判断数据是否有问题
        List<Product> products = result.getData();//得到商品列表
        List<SeckillProductVo> productVoList = todayList.stream().map(sp -> {
                    SeckillProductVo vo = new SeckillProductVo();
                    BeanUtils.copyProperties(sp, vo);
                    List<Product> list = products.stream().filter(p -> sp.getProductId().equals(p.getProductId())).collect(Collectors.toList());
                    if (list.size() > 0) {
                        Product product = list.get(0);
                        BeanUtils.copyProperties(product, vo);
                    }
                    vo.setSeckillId(sp.getSeckillId());
                    return vo;
                }).collect(Collectors.toList());    //两表缝合
        return productVoList;
    }


    @Override
    @Cacheable(key = "'selectByIdAndTime:' + #time + ':' + #seckillId")
    public SeckillProductVo selectByIdAndTime(Long seckillId, Integer time) {
        SeckillProduct seckillProduct=seckillProductMapper.selectByIdAndTime(seckillId, time); //查询秒杀货物秒杀表信息
        Result<List<Product>> result = productFeignApi.selectByIdList(Collections.singletonList(seckillProduct.getProductId()));  //查询货物信息
        if (result.hasError() || result.getData() == null || result.getData().size() == 0) {
            throw new BusinessException(new CodeMsg(result.getStatusCode(), result.getMsg()));
        }//没在货物表中查到数据就抛出异常
        Product product = result.getData().get(0);
        SeckillProductVo vo = new SeckillProductVo();
        BeanUtils.copyProperties(product, vo);
        BeanUtils.copyProperties(seckillProduct, vo);   //将货物信息和秒杀货物价格缝合起来
        return vo;
    }
    @CacheEvict(key = "'selectByIdAndTime:' + #time + ':' + #seckillId")
    @Override
    public int reduceStockCount(Long seckillId, Integer time) {
        return seckillProductMapper.reduceStockCount(seckillId);
    }


    @Override
    public SeckillProduct findById(Long seckillId) {
        return seckillProductMapper.selectById(seckillId);
    }

    @Override
    public void incryStockCount(Long seckillId) {
        seckillProductMapper.incrStock(seckillId);
    }
}
