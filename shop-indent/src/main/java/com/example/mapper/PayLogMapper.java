package com.example.mapper;

import com.example.common.entity.PayLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayLogMapper {

    int insertPayLog(PayLog payLog);
}
