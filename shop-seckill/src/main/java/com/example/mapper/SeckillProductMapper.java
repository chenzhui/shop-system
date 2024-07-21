package com.example.mapper;

import com.example.common.entity.SeckillProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeckillProductMapper {

    SeckillProduct selectByIdAndTime(@Param("seckillId") Long seckillId, @Param("time")Integer time);
    int reduceStockCount(@Param("seckillId") Long seckillId);

    SeckillProduct selectById(Long id);

    List<SeckillProduct> queryCurrentlySeckillProduct(Integer time);

    void incryStockCount(Long seckillId);

    int incrStock(Long seckillId);

}
