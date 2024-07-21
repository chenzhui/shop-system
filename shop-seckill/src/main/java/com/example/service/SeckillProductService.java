package com.example.service;



import com.example.common.entity.SeckillProduct;
import com.example.common.entity.SeckillProductVo;

import java.util.List;

public interface SeckillProductService {

    List<SeckillProductVo> selectTodayListByTime(Integer time);

    List<SeckillProductVo> selectTodayListByTimeFromRedis(Integer time);
    SeckillProductVo selectByIdAndTime(Long seckillId, Integer time);
    int reduceStockCount(Long seckillId, Integer time);
    SeckillProduct findById(Long seckillId);
    void incryStockCount(Long seckillId);

}
