package com.example.mapper;
import com.example.common.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderInfoMapper {
    int insertOrder(OrderInfo orderInfo);

    int updateCancelStatus(@Param("orderNo") String orderNo, @Param("status") Integer status);

    OrderInfo selectOrderInfoByNo(String orderNo);

}