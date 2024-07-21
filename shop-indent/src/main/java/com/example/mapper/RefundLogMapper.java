package com.example.mapper;

import com.example.common.entity.RefundLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefundLogMapper {

    int insertRefundLog(RefundLog refundLog);

    RefundLog selectRefundLog(String orderNo);
}
